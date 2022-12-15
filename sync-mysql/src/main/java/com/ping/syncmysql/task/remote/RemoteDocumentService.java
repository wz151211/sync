package com.ping.syncmysql.task.remote;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.ping.syncmysql.task.local.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("dm2")
public class RemoteDocumentService {
    @Autowired
    private RemoteDocumentMapper remoteDocumentMapper;

    @DS("dm2")
    public void save(DocumentEntity entity) {
        remoteDocumentMapper.insert(entity);
    }
}
