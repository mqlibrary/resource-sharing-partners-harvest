# Configuring the Institution

## Synchronisation Record Generation

An Alma record is an amalgamation of the Datastore representation of an organisation and the loading institutions configuration.

The instituion configuration needs to be placed in the ElasticSearch datastore at a location specific to your organisation's NUC. If your organisations NUC is 'NMQU', then you need to place the configuration at /partner-configs/config/NMQU:

```bash
http PUT localhost:9200/partner-configs/config/NMQU @/path/to/NMQU.json
```

**Sample NMQU.json:** (_do not use with comments_)

```json
{
    // base URI for link to the partner in Alma
    "linkBase": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/",

    // Currency to use
    "currency": "AUD",

    // Workflow settings
    "borrowingSupported": true,
    "borrowingWorkflow": "LADD_Borrowing",
    "lendingSupported": true,
    "lendingWorkflow": "LADD_Lending",
    "avgSupplyTime": 4,
    "deliveryDelay": 4,
    "locateProfileDesc": "LADD Locate Profile",
    "locateProfileValue": "LADD",
    "systemTypeDesc": "LADD",
    "systemTypeValue": "LADD",

    // ISO configuration settings
    "isoAlternativeDocumentDelivery": false,
    "isoIllServer": "nla.vdxhost.com",
    "isoIllPort": "1611",
    "isoRequestExpiryTypeDesc": "Expire by interest date",
    "isoRequestExpiryTypeValue": "INTEREST_DATE",
    "isoSendRequesterInformation": false,
    "isoSharedBarcodes": true,
    "isoSymbol": "NLA:NMQU",

    // Some flags to allow setting of the 'preferred' option for contact info
    "preferredAddressType": "shipping",
    "preferredPhoneType": "claimPhone",
    "preferredEmailType": "queries"
}
```
The following are valid preferred types:
- Address:
   - billing
   - claim
   - order
   - payment
   - returns
   - shipping
- Phone:
   - claimPhone
   - orderPhone
   - paymentPhone
   - returnsPhone
- Email:
   - claimMail
   - orderMail
   - paymentMail
   - queries
   - returnsMail

```
DATASTORE RECORD + INSTITUTION CONFIGURATION = ALMA RECORD
```

**DATASTORE RECORD:**

```json
{
    "partner_details": {
        "code": "NCN",
        "name": "Katie Zepps Nursing Library",
        "status": "ACTIVE",
        "profile_details": {
            "profile_type": "ISO",
            "iso_details": {
                "alternative_document_delivery": false,
                "ill_server": "nla.vdxhost.com",
                "ill_port": 1612,
                "iso_symbol": "NLA:NCN",
                "request_expiry_type": {
                    "value": "INTEREST_DATE",
                    "desc": "Expire by interest date"
                },
                "send_requester_information": false,
                "shared_barcodes": true,
                "ignore_shipping_cost_override": false
            }
        },
        "system_type": {
            "value": "LADD",
            "desc": "LADD"
        },
        "avg_supply_time": 4,
        "delivery_delay": 4,
        "currency": "AUD",
        "borrowing_supported": true,
        "borrowing_workflow": "LADD_Borrowing",
        "lending_supported": true,
        "lending_workflow": "LADD_Lending",
        "locate_profile": {
            "value": "LADD",
            "desc": "LADD Locate Profile"
        },
        "holding_code": "NCN"
    },
    "contact_info": {
        "address": [
            {
                "line1": "Level 6",
                "city": "PARRAMATTA",
                "line2": "9 Wentworth Street",
                "line3": null,
                "line4": null,
                "line5": null,
                "state_province": "NSW",
                "postal_code": "2150",
                "country": {
                    "value": "AUS",
                    "desc": "Australia"
                },
                "start_date": "2017-09-07Z",
                "address_type": ["ALL"],
                "preferred": false
            },
            {
                "line1": "PO Box 650",
                "city": "PARRAMATTA",
                "line2": null,
                "line3": null,
                "line4": null,
                "line5": null,
                "state_province": "NSW",
                "postal_code": "2124",
                "country": {
                    "value": "AUS",
                    "desc": "Australia"
                },
                "start_date": "2017-09-07Z",
                "address_type": ["shipping"],
                "preferred": false
            }
        ],
        "phone": [
            {
                "phone_number": "02 9745 7536",
                "phone_type": ["claim_phone", "order_phone", "payment_phone", "returns_phone"],
                "preferred": false,
                "preferredSMS": null
            }
        ],
        "email": [
            {
                "email_address": "library.technician@acn.edu.au",
                "description": null,
                "email_type": ["ALL"],
                "preferred": false
            }
        ]
    },
    "note": [],
    "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/NCN"
}
```

**+**

**INSTITUTION CONFIGURATION**

```json
{
    "linkBase": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/",
    "currency": "AUD",
    "borrowingSupported": true,
    "borrowingWorkflow": "LADD_Borrowing",
    "lendingSupported": true,
    "lendingWorkflow": "LADD_Lending",
    "avgSupplyTime": 4,
    "deliveryDelay": 4,
    "locateProfileDesc": "LADD Locate Profile",
    "locateProfileValue": "LADD",
    "systemTypeDesc": "LADD",
    "systemTypeValue": "LADD",
    "isoAlternativeDocumentDelivery": false,
    "isoIllServer": "nla.vdxhost.com",
    "isoIllPort": "1611",
    "isoRequestExpiryTypeDesc": "Expire by interest date",
    "isoRequestExpiryTypeValue": "INTEREST_DATE",
    "isoSendRequesterInformation": false,
    "isoSharedBarcodes": true,
    "isoSymbol": "NLA:NMQU",
    "preferredAddressType": "shipping",
    "preferredPhoneType": "ill",
    "preferredEmailType": "ill"
}
```

**=**

**ALMA RECORD**

```json
{
    "partner_details": {
        "code": "NCN",
        "name": "Katie Zepps Nursing Library",
        "status": "ACTIVE",
        "profile_details": {
            "profile_type": "ISO",
            "iso_details": {
                "alternative_document_delivery": false,
                "ill_server": "nla.vdxhost.com",
                "ill_port": 1612,
                "iso_symbol": "NLA:NCN",
                "request_expiry_type": {
                    "value": "INTEREST_DATE",
                    "desc": "Expire by interest date"
                },
                "send_requester_information": false,
                "shared_barcodes": true,
                "ignore_shipping_cost_override": false
            }
        },
        "system_type": {
            "value": "LADD",
            "desc": "LADD"
        },
        "avg_supply_time": 4,
        "delivery_delay": 4,
        "currency": "AUD",
        "borrowing_supported": true,
        "borrowing_workflow": "LADD_Borrowing",
        "lending_supported": true,
        "lending_workflow": "LADD_Lending",
        "locate_profile": {
            "value": "LADD",
            "desc": "LADD Locate Profile"
        },
        "holding_code": "NCN"
    },
    "contact_info": {
        "address": [
            {
                "line1": "Level 6",
                "city": "PARRAMATTA",
                "line2": "9 Wentworth Street",
                "line3": null,
                "line4": null,
                "line5": null,
                "state_province": "NSW",
                "postal_code": "2150",
                "country": {
                    "value": "AUS",
                    "desc": "Australia"
                },
                "start_date": "2017-09-07Z",
                "address_type": ["ALL"],
                "preferred": false
            },
            {
                "line1": "PO Box 650",
                "city": "PARRAMATTA",
                "line2": null,
                "line3": null,
                "line4": null,
                "line5": null,
                "state_province": "NSW",
                "postal_code": "2124",
                "country": {
                    "value": "AUS",
                    "desc": "Australia"
                },
                "start_date": "2017-09-07Z",
                "address_type": ["shipping"],
                "preferred": false
            }
        ],
        "phone": [
            {
                "phone_number": "02 9745 7536",
                "phone_type": ["claim_phone", "order_phone", "payment_phone", "returns_phone"],
                "preferred": false,
                "preferredSMS": null
            }
        ],
        "email": [
            {
                "email_address": "library.technician@acn.edu.au",
                "description": null,
                "email_type": ["ALL"],
                "preferred": false
            }
        ]
    },
    "note": [],
    "link": "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/partners/NCN"
}
```
