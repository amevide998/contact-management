#Address API Spec

### Create Address
End Point : POST /api/contacts/addresses

Request Header :
- X-API-TOKEN : Token

Request Body : 
```json
{
  "street": "street address", 
  "city": "malang",
  "province": "east java",
  "country": "indonesia",
  "postalCode": "22211"
}
```

Response Body (Success) : 
```json
{
  "data": {
    "id": "random",
    "street": "street address",
    "city": "malang",
    "province": "east java",
    "country": "indonesia",
    "postalCode": "22211"
  }
}
```

Response Body (Failed) :
```json
{
  "error": "error message"
}
```

### Update Address
End Point : PUT /api/contacts/{idContacts}/addresses/{idAddress}

Request Header :
- X-API-TOKEN : Token

Request Body :
```json
{
  "street": "street address", 
  "city": "malang",
  "province": "east java",
  "country": "indonesia",
  "postalCode": "22211"
}
```

Response Body (Success) :
```json
{
  "data": {
    "id": "random",
    "street": "street address",
    "city": "malang",
    "province": "east java",
    "country": "indonesia",
    "postalCode": "22211"
  }
}
```

Response Body (Failed) :
```json
{
  "error": "error message"
}
```

### Get Address
End Point : GET /api/contacts/{idContacts}/addresses/{idAddress}

Request Header :
- X-API-TOKEN : Token

Response Body (Success) :
```json
{
  "data": {
    "id": "random",
    "street": "street address",
    "city": "malang",
    "province": "east java",
    "country": "indonesia",
    "postalCode": "22211"
  }
}
```

Response Body (Failed) :
```json
{
  "error": "error message"
}
```

### Remove Address
End Point : DELETE /api/contacts/{idContacts}/addresses/{idAddress}

Request Header :
- X-API-TOKEN : Token

Response Body (Success) :
```json
{
  "data": "Ok"
}
```
Response Body (Failed) :
```json
{
  "error": "Error message"
}
```
### List Address
End Point : GET /api/contacts/{idContacts}/addresses

Request Header :
- X-API-TOKEN : Token


Response Body (Success) :
```json
{
  "data": [
      {
    "id": "random",
    "street": "street address",
    "city": "malang",
    "province": "east java",
    "country": "indonesia",
    "postalCode": "22211"
    }
  ]
}
```
Response Body (Failed) :
```json
{
  "error": "Error message"
}
```