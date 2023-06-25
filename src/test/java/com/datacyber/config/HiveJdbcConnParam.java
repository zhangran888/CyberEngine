package com.datacyber.config;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class HiveJdbcConnParam extends BaseJdbcConnParam {
    /**
     * enable kerberos authentication
     */
    private boolean enableKerberos;

    /**
     * principal
     */
    private String principal;

    /**
     * kbr5 file path in dick
     */
    private String kbr5FilePath;

    /**
     * keytab file path in dick
     */
    private String keytabFilePath;
}
