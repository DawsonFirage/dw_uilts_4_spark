package com.dwsn.holidayApi.dto;

import lombok.Data;

@Data
public class HolidayInfoDTO {

    /**
     * {
     *       "counties": null,
     *       "countryCode": "CN",
     *       "date": "2025-01-01",
     *       "fixed": false,
     *       "global": true,
     *       "launchYear": null,
     *       "localName": "元旦",
     *       "name": "New Year's Day",
     *       "types": [
     *          "Public"
     *       ]
     *    }
     */

    private String counties;
    private String countryCode;
    private String date;
    private boolean fixed;
    private boolean global;
    private String launchYear;
    private String localName;
    private String name;
    private String[] types;

}
