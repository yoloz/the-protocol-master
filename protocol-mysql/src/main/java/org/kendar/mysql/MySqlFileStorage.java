package org.kendar.mysql;

import org.kendar.sql.jdbc.SelectResult;
import org.kendar.sql.jdbc.storage.JdbcFileStorage;
import org.kendar.sql.jdbc.storage.JdbcRequest;
import org.kendar.sql.jdbc.storage.JdbcResponse;
import org.kendar.storage.CompactLine;
import org.kendar.storage.StorageItem;
import org.kendar.utils.JsonMapper;

import java.nio.file.Path;
import java.util.List;

public class MySqlFileStorage extends JdbcFileStorage {
    private static final String SELECT_TRANS = "SELECT @@session.transaction_read_only";
    private static final String SELECT_TRANS_RESULT = "{\n" +
            "      \"records\" : [ [ \"0\" ] ],\n" +
            "      \"metadata\" : [ {\n" +
            "        \"columnName\" : \"@@session.transaction_read_only\",\n" +
            "        \"columnLabel\" : \"@@session.transaction_read_only\",\n" +
            "        \"byteData\" : false,\n" +
            "        \"catalogName\" : \"\",\n" +
            "        \"schemaName\" : \"\",\n" +
            "        \"tableName\" : \"\",\n" +
            "        \"columnDisplaySize\" : 19,\n" +
            "        \"columnType\" : \"BIGINT\",\n" +
            "        \"precision\" : 19\n" +
            "      } ],\n" +
            "      \"count\" : 1,\n" +
            "      \"intResult\" : false,\n" +
            "      \"lastInsertedId\" : 0\n" +
            "    }";

    public MySqlFileStorage(String targetDir) {
        super(targetDir);
    }

    public MySqlFileStorage(Path targetDir) {
        super(targetDir);
    }

    @Override
    protected boolean shouldNotSave(CompactLine cl, List<CompactLine> compactLines, StorageItem<JdbcRequest, JdbcResponse> item,
                                    List<StorageItem<JdbcRequest, JdbcResponse>> loadedData) {
        if (useFullData) return false;
        var shouldNotSave = super.shouldNotSave(cl, compactLines, item, loadedData);
        if (cl.getTags().get("query").toUpperCase().startsWith(SELECT_TRANS.toUpperCase())) {
            if (item != null) {
                cl.getTags().put("isIntResult", "true");
                cl.getTags().put("resultsCount", item.getOutput().getSelectResult().getRecords().get(0).get(0));
            }
            return true;
        }
        return shouldNotSave;
    }

    @Override
    protected StorageItem beforeSendingReadResult(StorageItem<JdbcRequest, JdbcResponse> si) {
        if (si.getInput().getQuery().toUpperCase().startsWith(SELECT_TRANS.toUpperCase())) {
            var selectResult = new JsonMapper().deserialize(SELECT_TRANS_RESULT, SelectResult.class);
            var realResultValue = si.getOutput().getIntResult();
            selectResult.getRecords().get(0).set(0, realResultValue + "");
            si.getOutput().setSelectResult(selectResult);
            si.getOutput().setIntResult(0);
        }
        return si;
    }
}
