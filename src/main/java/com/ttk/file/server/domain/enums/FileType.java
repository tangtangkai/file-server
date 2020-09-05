package com.ttk.file.server.domain.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum FileType {

    /*图片1开头*/
    JPG(101, "image/jpeg", "jpg格式图片", ".jpg"),
    PNG(102, "image/png", "png格式图片", ".png");

    private final int code;

    private final String contentType;

    private final String describe;

    private final String suffix;


    FileType(int code, String contentType, String describe, String suffix) {
        this.code = code;
        this.contentType = contentType;
        this.describe = describe;
        this.suffix = suffix;
    }

    /**
     * 判断是否为当前值
     *
     * @param code
     * @return
     */
    public boolean isMe(int code) {
        return this.code == code;
    }

    public boolean isMe(String suffix) {
        return suffix.toLowerCase().equals(this.suffix);
    }

    /**
     * 查找对应的枚举类,根据code
     *
     * @param code
     * @return
     */
    public static Optional<FileType> valueOf(int code) {
        for (FileType type : FileType.values()) {
            if (type.isMe(code)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * 查找对应的枚举，根据文件名后缀
     *
     * @param suffix
     * @return
     */
    public static Optional<FileType> valueOfBySuffix(String suffix) {
        for (FileType type : FileType.values()) {
            if (type.isMe(suffix)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }


}
