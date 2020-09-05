package com.ttk.file.server.domain.enums;

import lombok.Getter;

/**
 * @author root
 */
@Getter
public enum DeleteStatus {

    NOT_DELETE(0),
    DELETED(1);

    private final int status;

    DeleteStatus(int status) {
        this.status = status;
    }
}
