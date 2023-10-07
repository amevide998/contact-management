# Contact API Spec 

### Create Contact
End Point : POST /api/contacts

Request Header : 
- X-API-TOKEN : Token 

Request Body :
```json
{
  "firstname": "hadin",
  "lastname": "davidi",
  "email": "hdsvidi@gmail.com",
  "phone": "08999999999"
}
```

Response Body (Success) :
```json
{
  "data" : {
    "id": "random",
    "firstname": "hadin",
    "lastname": "davidi",
    "email": "hdsvidi@gmail.com",
    "phone": "08999999999"
  }
}
```

Response Body (Failed) : 
```json
{
  "errors": "error message"
}
```

### Update Contact 
End Point : PUT /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token

Request Body :
```json
{
  "firstname": "hadin",
  "lastname": "davidi",
  "email": "hdsvidi@gmail.com",
  "phone": "08999999999"
}
```

Response Body (Success) :
```json
{
  "data" : {
    "id": "random",
    "firstname": "hadin",
    "lastname": "davidi",
    "email": "hdsvidi@gmail.com",
    "phone": "08999999999"
  }
}
```

Response Body (Failed) :
```json
{
  "errors": "error message"
}
```

### Get Contact
End Point : GET /api/contacts

Request Header :
- X-API-TOKEN : Token

Request Body : None

Response Body (Success) :
```json
{
  "data" : {
    "id": "random",
    "firstname": "hadin",
    "lastname": "davidi",
    "email": "hdsvidi@gmail.com",
    "phone": "08999999999"
  }
}
```

Response Body (Failed) :
```json
{
  "errors": "error message"
}
```

### Search Contact
End Point : GET /api/contacts

Request Header :
- X-API-TOKEN : Token

Query Param :

- name : String, firstname or lastname - OPTIONAL
- phone : String, contact phone - OPTIONAL 
- email : String, contact email - OPTIONAL 
- page : Integer, start from 0, default 0  
- size : 10 

Request Body :

Response Body (Success) :
```json
{
  "data" : [{
    "id": "random",
    "firstname": "hadin",
    "lastname": "davidi",
    "email": "hdsvidi@gmail.com",
    "phone": "08999999999"
  }],
  "paging" : {
    "current_page" : 0,
    "total_page": 10,
    "size": 10
  }
}
```
Response Body (Failed) :
```json
{
  "errors": "error message"
}
```


### Remove Contact
End Point : DELETE /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token

Request Body :

Response Body (Success) :
```json
{
  "data": "OK"
}
```

Response Body (Failed) : 
```json
{
  "errors": "error message"
}
```

