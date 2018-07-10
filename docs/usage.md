# Using the Sync Server

_The examples in this document use [HTTPie](https://httpie.org)._

For the operations {nuc} should be replaced with your institutions NUC symbol.

All operations require the Authorization HTTP header with an API key for your Alma instances. The API key must have read/write access to the Resource Partners API. The header works exactly the same as using any Alma API:

```bash
Authorization: apikey {APIKEY}
```

There are __5__ operations that you can use the Sync Server for:

1. GET /partner-sync/{nuc}/__preview__

    _Provide a list of Partner records that will be updated in Alma if you were to trigger a Sync. This gives you the list without changing anything in Alma._

    Sample request:
    ```bash
    http localhost:8080/partner-sync/NMQU/preview Authorization:"apikey l7xx123a123b123c123d123e123f123g123h"
    ```

    Sample output:
    ```json
    {
      "partner": [
        {
          "contact_info": {
            "addresses": {
              "address": [
                {
                  "address_types": {
                    "address_type": [
                      "ALL"
                    ]
                  },
                  "city": "BOX HILL",
                  "country": {
                    "desc": "Australia",
                    "value": "AUS"
                  },
                  "line1": "Box Hill Institute Library - Interlibrary Loans",
                  "line2": "465 Elgar Road",
                  "postal_code": "3128",
                  "preferred": false,
                  "state_province": "VIC"
                },
                {
                  "address_types": {
                    "address_type": [
                      "shipping"
                    ]
                  },
                  "city": "BOX HILL",
                  "country": {
                    "desc": "Australia",
                    "value": "AUS"
                  },
                  "line1": "Box Hill Institute Library - Interlibrary Loans",
                  "line2": "Private Bag 2014",
                  "postal_code": "3128",
                  "preferred": false,
                  "state_province": "VIC"
                }
              ]
            },
            "emails": {
              "email": [
                {
                  "email_address": "ill@boxhill.edu.au",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                }
              ]
            },
            "phones": {
              "phone": [
                {
                  "phone_number": "03 9286 9283",
                  "phone_types": {
                    "phone_type": [
                      "claim_phone",
                      "order_phone",
                      "payment_phone",
                      "returns_phone"
                    ]
                  },
                  "preferred": false
                }
              ]
            }
          },
          "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/VBHE",
          "notes": {
          },
          "partner_details": {
            "avg_supply_time": 4,
            "borrowing_supported": true,
            "borrowing_workflow": "LADD_Borrowing",
            "code": "VBHE",
            "currency": "AUD",
            "delivery_delay": 4,
            "holding_code": "VBHE",
            "lending_supported": true,
            "lending_workflow": "LADD_Lending",
            "locate_profile": {
              "desc": "LADD Locate Profile",
              "value": "LADD"
            },
            "name": "Box Hill Institute",
            "profile_details": {
              "iso_details": {
                "alternative_document_delivery": false,
                "ill_port": 1611,
                "ill_server": "nla.vdxhost.com",
                "iso_symbol": "NLA:VBHE",
                "request_expiry_type": {
                  "desc": "Expire by interest date",
                  "value": "INTEREST_DATE"
                },
                "send_requester_information": false,
                "shared_barcodes": true
              },
              "profile_type": "ISO"
            },
            "status": "ACTIVE",
            "system_type": {
              "desc": "LADD",
              "value": "LADD"
            }
          }
        },
        {
          "contact_info": {
            "addresses": {
              "address": [
                {
                  "address_types": {
                    "address_type": [
                      "ALL"
                    ]
                  },
                  "city": "Wellington",
                  "country": {
                    "desc": "New Zealand",
                    "value": "NZL"
                  },
                  "line1": "Level 9",
                  "line2": "Solnet House",
                  "line3": "70 The Terrace",
                  "postal_code": "6011",
                  "preferred": false
                },
                {
                  "address_types": {
                    "address_type": [
                      "shipping"
                    ]
                  },
                  "city": "Wellington",
                  "country": {
                    "desc": "New Zealand",
                    "value": "NZL"
                  },
                  "line1": "PO Box 2590",
                  "postal_code": "6140",
                  "preferred": false
                }
              ]
            },
            "emails": {
              "email": [
                {
                  "email_address": "library@lawcom.govt.nz",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                },
                {
                  "email_address": "library@lawcom.govt.nz",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                }
              ]
            },
            "phones": {
              "phone": [
                {
                  "phone_number": "+64 4 914 4843",
                  "phone_types": {
                    "phone_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                },
                {
                  "phone_number": "+64 4 914 4831",
                  "phone_types": {
                    "phone_type": [
                      "claim_phone",
                      "order_phone",
                      "payment_phone",
                      "returns_phone"
                    ]
                  },
                  "preferred": false
                }
              ]
            }
          },
          "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/NLNZ:WLC",
          "notes": {
          },
          "partner_details": {
            "avg_supply_time": 4,
            "borrowing_supported": true,
            "borrowing_workflow": "LADD_Borrowing",
            "code": "NLNZ:WLC",
            "currency": "AUD",
            "delivery_delay": 4,
            "holding_code": "NLNZ:WLC",
            "lending_supported": true,
            "lending_workflow": "LADD_Lending",
            "locate_profile": {
              "desc": "LADD Locate Profile",
              "value": "LADD"
            },
            "name": "Law Commission Library",
            "profile_details": {
              "iso_details": {
                "alternative_document_delivery": false,
                "ill_port": 1611,
                "ill_server": "nla.vdxhost.com",
                "iso_symbol": "NLNZ:WLC",
                "request_expiry_type": {
                  "desc": "Expire by interest date",
                  "value": "INTEREST_DATE"
                },
                "send_requester_information": false,
                "shared_barcodes": true
              },
              "profile_type": "ISO"
            },
            "status": "ACTIVE",
            "system_type": {
              "desc": "LADD",
              "value": "LADD"
            }
          }
        }
      ]
    }
    ```

1. GET /partner-sync/{nuc}/__changes__

    _Provides a list of changes between the datastore and your Alma instance._

    Sample request:
    ```bash
    http localhost:8080/partner-sync/NMQU/changes Authorization:"apikey l7xx123a123b123c123d123e123f123g123h"
    ```

    Sample output:
    ```json
    [
        {
            "after": "ACTIVE",
            "before": "INACTIVE",
            "field": "status",
            "nuc": "NABH",
            "source_system": "NMQU",
            "time": "2018-05-21T23:17:57+1000"
        },
        {
            "after": "{\"preferred\":false,\"email_address\":\"kal@lcc.co.nz\",\"email_types\":{\"email_type\":[\"ALL\"]}}",
            "before": null,
            "field": "email",
            "nuc": "NLNZ:WLCC",
            "source_system": "NMQU",
            "time": "2018-05-21T23:17:57+1000"
        }
    ]
    ```

1. GET /partner-sync/{nuc}/__orphaned__

    _Provides a list of partners that are in Alma, but not in the Datastore._

    Sample request:
    ```bash
    http localhost:8080/partner-sync/NMQU/orphaned Authorization:"apikey l7xx123a123b123c123d123e123f123g123h"
    ```

    Sample response:
    ```json
    [
        {
            "contact_info": {
                "addresses": {},
                "emails": {},
                "phones": {}
            },
            "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/QACU",
            "notes": {},
            "partner_details": {
                "avg_supply_time": 4,
                "borrowing_supported": true,
                "borrowing_workflow": "LADD_Borrowing",
                "code": "QACU",
                "currency": "AUD",
                "delivery_delay": 4,
                "holding_code": "QACU",
                "lending_supported": true,
                "lending_workflow": "LADD_Lending",
                "locate_profile": {
                    "desc": "LADD Locate Profile",
                    "value": "LADD"
                },
                "name": "Australian Catholic University Interlibrary Loans",
                "profile_details": {
                    "iso_details": {
                        "alternative_document_delivery": false,
                        "ill_port": 1611,
                        "ill_server": "nla.vdxhost.com",
                        "iso_symbol": "NLA:QACU",
                        "request_expiry_type": {
                            "desc": "Expire by interest date",
                            "value": "INTEREST_DATE"
                        },
                        "send_requester_information": false,
                        "shared_barcodes": true
                    },
                    "profile_type": "ISO"
                },
                "status": "ACTIVE",
                "system_type": {
                    "desc": "LADD",
                    "value": "LADD"
                }
            }
        }
    ]
    ```

1. GET /partner-sync/{nuc}/__sync__

    _Initiate synchronisation with Alma. Records that are different between the Datastore and Alma will be updated with data from the Datastore. The response is the list of Partners that are updated. This operation also expires the cache (see expirecache operation below)._

    Sample request:
    ```bash
    http localhost:8080/partner-sync/NMQU/sync Authorization:"apikey l7xx123a123b123c123d123e123f123g123h"
    ```
    
    Sample output:
    ```json
    {
      "partner": [
        {
          "contact_info": {
            "addresses": {
              "address": [
                {
                  "address_types": {
                    "address_type": [
                      "ALL"
                    ]
                  },
                  "city": "BOX HILL",
                  "country": {
                    "desc": "Australia",
                    "value": "AUS"
                  },
                  "line1": "Box Hill Institute Library - Interlibrary Loans",
                  "line2": "465 Elgar Road",
                  "postal_code": "3128",
                  "preferred": false,
                  "state_province": "VIC"
                },
                {
                  "address_types": {
                    "address_type": [
                      "shipping"
                    ]
                  },
                  "city": "BOX HILL",
                  "country": {
                    "desc": "Australia",
                    "value": "AUS"
                  },
                  "line1": "Box Hill Institute Library - Interlibrary Loans",
                  "line2": "Private Bag 2014",
                  "postal_code": "3128",
                  "preferred": false,
                  "state_province": "VIC"
                }
              ]
            },
            "emails": {
              "email": [
                {
                  "email_address": "ill@boxhill.edu.au",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                }
              ]
            },
            "phones": {
              "phone": [
                {
                  "phone_number": "03 9286 9283",
                  "phone_types": {
                    "phone_type": [
                      "claim_phone",
                      "order_phone",
                      "payment_phone",
                      "returns_phone"
                    ]
                  },
                  "preferred": false
                }
              ]
            }
          },
          "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/VBHE",
          "notes": {
          },
          "partner_details": {
            "avg_supply_time": 4,
            "borrowing_supported": true,
            "borrowing_workflow": "LADD_Borrowing",
            "code": "VBHE",
            "currency": "AUD",
            "delivery_delay": 4,
            "holding_code": "VBHE",
            "lending_supported": true,
            "lending_workflow": "LADD_Lending",
            "locate_profile": {
              "desc": "LADD Locate Profile",
              "value": "LADD"
            },
            "name": "Box Hill Institute",
            "profile_details": {
              "iso_details": {
                "alternative_document_delivery": false,
                "ill_port": 1611,
                "ill_server": "nla.vdxhost.com",
                "iso_symbol": "NLA:VBHE",
                "request_expiry_type": {
                  "desc": "Expire by interest date",
                  "value": "INTEREST_DATE"
                },
                "send_requester_information": false,
                "shared_barcodes": true
              },
              "profile_type": "ISO"
            },
            "status": "ACTIVE",
            "system_type": {
              "desc": "LADD",
              "value": "LADD"
            }
          }
        },
        {
          "contact_info": {
            "addresses": {
              "address": [
                {
                  "address_types": {
                    "address_type": [
                      "ALL"
                    ]
                  },
                  "city": "Wellington",
                  "country": {
                    "desc": "New Zealand",
                    "value": "NZL"
                  },
                  "line1": "Level 9",
                  "line2": "Solnet House",
                  "line3": "70 The Terrace",
                  "postal_code": "6011",
                  "preferred": false
                },
                {
                  "address_types": {
                    "address_type": [
                      "shipping"
                    ]
                  },
                  "city": "Wellington",
                  "country": {
                    "desc": "New Zealand",
                    "value": "NZL"
                  },
                  "line1": "PO Box 2590",
                  "postal_code": "6140",
                  "preferred": false
                }
              ]
            },
            "emails": {
              "email": [
                {
                  "email_address": "library@lawcom.govt.nz",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                },
                {
                  "email_address": "library@lawcom.govt.nz",
                  "email_types": {
                    "email_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                }
              ]
            },
            "phones": {
              "phone": [
                {
                  "phone_number": "+64 4 914 4843",
                  "phone_types": {
                    "phone_type": [
                      "ALL"
                    ]
                  },
                  "preferred": false
                },
                {
                  "phone_number": "+64 4 914 4831",
                  "phone_types": {
                    "phone_type": [
                      "claim_phone",
                      "order_phone",
                      "payment_phone",
                      "returns_phone"
                    ]
                  },
                  "preferred": false
                }
              ]
            }
          },
          "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/NLNZ:WLC",
          "notes": {
          },
          "partner_details": {
            "avg_supply_time": 4,
            "borrowing_supported": true,
            "borrowing_workflow": "LADD_Borrowing",
            "code": "NLNZ:WLC",
            "currency": "AUD",
            "delivery_delay": 4,
            "holding_code": "NLNZ:WLC",
            "lending_supported": true,
            "lending_workflow": "LADD_Lending",
            "locate_profile": {
              "desc": "LADD Locate Profile",
              "value": "LADD"
            },
            "name": "Law Commission Library",
            "profile_details": {
              "iso_details": {
                "alternative_document_delivery": false,
                "ill_port": 1611,
                "ill_server": "nla.vdxhost.com",
                "iso_symbol": "NLNZ:WLC",
                "request_expiry_type": {
                  "desc": "Expire by interest date",
                  "value": "INTEREST_DATE"
                },
                "send_requester_information": false,
                "shared_barcodes": true
              },
              "profile_type": "ISO"
            },
            "status": "ACTIVE",
            "system_type": {
              "desc": "LADD",
              "value": "LADD"
            }
          }
        }
      ]
    }
    ```


1. GET /partner-sync/{nuc}/__expirecache__

    _When an initial request is made, Partners are retrieved from Alma and cached. Subsequent API calls make use of the cache. If you wish to refresh the Partner records in memory, you can expire the cache by making a request to this endpoint. There is no data in the response._

    Sample request:
    ```bash
    http localhost:8080/partner-sync/NMQU/expirecache Authorization:"apikey l7xx123a123b123c123d123e123f123g123h"
    ```
    
