# User API Spec

### Register User
End Point : POST /api/users
Request Body : 
```json
{
  "username": "hdscode", 
  "password": "12345",
  "name": "hadin davidi sianturi"
}
```
Response Body (Success) : 
```json
{
  "Data": "Ok"
}
```
Response Body (Failed) :
```json
{
  "errors": "error message"
}
```

### Login User
End Point : POST /api/auth/login
Request Body :
```json
{
  "username": "hdscode", 
  "password": "12345"
}
```
Response Body (Success) :
```json
{
  "Data": {
    "token": "TOKEN",
    "expiredAt": 12321321
  }
}
```
Response Body (Failed) :
```json
{
  "errors": "error message"
}
```

### Get User
End Point : GET /api/users/current

Request Body : None

Response Body (Success) :
```json
{
  "Data": {
    "username": "hdscode",
    "name": "hadin davidi"
  }
}
```
Response Body (Failed) :
```json
{
  "errors": "unauthorize"
}
```

### Update User
End Point : PATCH /api/users/current

Request Header : 
- X-APi-TOKEN : Token

Request Body :
```javascript
{
  "name": "hadin davidi",  // put buat update
  "password": "new password" // put buat update
}
```
Response Body (Success) :
```json
{
  "Data": {
    "username": "hdscode",
    "name": "hadin davidi"
  }
}
```
Response Body (Failed) :
```json
{
  "errors": "unauthorize"
}
```

### Logout User
Endpoint : DELETE /api/auth/logout
Request Header:
- X-API-TOKE : TOKEN