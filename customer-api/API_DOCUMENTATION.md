# Customer API Documentation

## Base URL
`http://localhost:8080/api/customers`

## Authentication
No authentication required for this API.

## Endpoints

### 1. Get All Customers
**GET** `/api/customers`

**Description:** Retrieve all customers

**Response: 200 OK**
```json
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Doe",
        "email": "john.doe@example.com",
        "phone": "+1-555-0101",
        "address": "123 Main St, New York, NY 10001",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 2,
        "customerCode": "C002",
        "fullName": "Jane Smith",
        "email": "jane.smith@example.com",
        "phone": "+1-555-0102",
        "address": "456 Oak Ave, Los Angeles, CA 90001",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 3,
        "customerCode": "C003",
        "fullName": "Bob Johnson",
        "email": "bob.johnson@example.com",
        "phone": "+1-555-0103",
        "address": "789 Pine Rd, Chicago, IL 60601",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 4,
        "customerCode": "C004",
        "fullName": "Alice Brown",
        "email": "alice.brown@example.com",
        "phone": "+1-555-0104",
        "address": "321 Elm St, Houston, TX 77001",
        "status": "INACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 5,
        "customerCode": "C005",
        "fullName": "Charlie Wilson",
        "email": "charlie.wilson@example.com",
        "phone": "+1-555-0105",
        "address": "654 Maple Dr, Phoenix, AZ 85001",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    }
]
```

### 2. Get Customer by ID
**GET** `/api/customers/{id}`

**Response: 200 OK**
```json
{
    "id": 1,
    "customerCode": "C001",
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0101",
    "address": "123 Main St, New York, NY 10001",
    "status": "ACTIVE",
    "createdAt": "2025-12-06T20:34:30"
}
```
### 3. Create New Customer
**POST** `/api/customers/`

**Response: 201 Created**
```json
{
    "id": 6,
    "customerCode": "C006",
    "fullName": "Trường Sơn Thạch",
    "email": "tst@example.com",
    "phone": "+01012230290",
    "address": "Ho Chi Minh City",
    "status": "ACTIVE",
    "createdAt": "2025-12-13T00:20:46.8587399"
}
```

### 4.Update Customer
**PUT** `/api/customers/{id}`

**Response: 200 OK**
```json
{
    "id": 2,
    "customerCode": "C002",
    "fullName": "Jane Smith Update",
    "email": "jane.smith.upd@example.com",
    "phone": "+0123456789",
    "address": "Washington D.C",
    "status": "ACTIVE",
    "createdAt": "2025-12-06T20:34:30"
}
```

### 5. Partical Update Customer
**PATCH** `/api/customers/{id}`

**Response: 200 OK**
```json
{
    "id": 6,
    "customerCode": "C006",
    "fullName": "Trường Sơn Thạch",
    "email": "tst.hoc.upd@example.com",
    "phone": "+01012230290",
    "address": "Ho Chi Minh City",
    "status": "ACTIVE",
    "createdAt": "2025-12-13T00:20:47"
}
```

### 6. Delete Customer
**DELETE** `/api/customers/{id}`

**Response: 200 OK**
```json
{
    "message": "Customer deleted successfully"
}
```

### 7. Search Customers
**GET** `/api/customers/search?keyword=tst`

**Response: 200 OK**
```json
[
    {
        "id": 6,
        "customerCode": "C006",
        "fullName": "Trường Sơn Thạch",
        "email": "tst.hoc.upd@example.com",
        "phone": "+01012230290",
        "address": "Ho Chi Minh City",
        "status": "ACTIVE",
        "createdAt": "2025-12-13T00:20:47"
    }
]
```

### 8. Filter by Status
**GET** `/api/customers/status/{status}`

**Response: 200 OK**
```json
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Partially Updated",
        "email": "john17.doe@example.com",
        "phone": "+9876543210",
        "address": null,
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 2,
        "customerCode": "C002",
        "fullName": "Jane Smith Update",
        "email": "jane.smith.upd@example.com",
        "phone": "+0123456789",
        "address": "Washington D.C",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 3,
        "customerCode": "C003",
        "fullName": "Bob Johnson",
        "email": "bob.johnson@example.com",
        "phone": "+1-555-0103",
        "address": "789 Pine Rd, Chicago, IL 60601",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 5,
        "customerCode": "C005",
        "fullName": "Charlie Wilson",
        "email": "charlie.wilson@example.com",
        "phone": "+1-555-0105",
        "address": "654 Maple Dr, Phoenix, AZ 85001",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 6,
        "customerCode": "C006",
        "fullName": "Trường Sơn Thạch",
        "email": "tst.hoc.upd@example.com",
        "phone": "+01012230290",
        "address": "Ho Chi Minh City",
        "status": "ACTIVE",
        "createdAt": "2025-12-13T00:20:47"
    }
]
```

### 9. Advanced Search
**GET** `/api/customers/advanced-search?name=john&email=example&status=ACTIVE`

**Response: 200 OK**
```json
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Partially Updated",
        "email": "john17.doe@example.com",
        "phone": "+9876543210",
        "address": null,
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 3,
        "customerCode": "C003",
        "fullName": "Bob Johnson",
        "email": "bob.johnson@example.com",
        "phone": "+1-555-0103",
        "address": "789 Pine Rd, Chicago, IL 60601",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    }
]
```

### 10.Pagination
**GET** `/api/customers/paginated?page=0&size=5`

**Response: 200 OK**
```json
{
    "totalItems": 5,
    "totalPages": 1,
    "pageSize": 5,
    "customers": [
        {
            "id": 1,
            "customerCode": "C001",
            "fullName": "John Partially Updated",
            "email": "john17.doe@example.com",
            "phone": "+9876543210",
            "address": null,
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 2,
            "customerCode": "C002",
            "fullName": "Jane Smith Update",
            "email": "jane.smith.upd@example.com",
            "phone": "+0123456789",
            "address": "Washington D.C",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 3,
            "customerCode": "C003",
            "fullName": "Bob Johnson",
            "email": "bob.johnson@example.com",
            "phone": "+1-555-0103",
            "address": "789 Pine Rd, Chicago, IL 60601",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 5,
            "customerCode": "C005",
            "fullName": "Charlie Wilson",
            "email": "charlie.wilson@example.com",
            "phone": "+1-555-0105",
            "address": "654 Maple Dr, Phoenix, AZ 85001",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 6,
            "customerCode": "C006",
            "fullName": "Trường Sơn Thạch",
            "email": "tst.hoc.upd@example.com",
            "phone": "+01012230290",
            "address": "Ho Chi Minh City",
            "status": "ACTIVE",
            "createdAt": "2025-12-13T00:20:47"
        }
    ],
    "currentPage": 0
}
```

### 11. Sorting 
**GET** `/api/customers/sorted?sortBy=fullName&sortDir=asc`

**Response: 200 OK**
```json
[
    {
        "id": 3,
        "customerCode": "C003",
        "fullName": "Bob Johnson",
        "email": "bob.johnson@example.com",
        "phone": "+1-555-0103",
        "address": "789 Pine Rd, Chicago, IL 60601",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 5,
        "customerCode": "C005",
        "fullName": "Charlie Wilson",
        "email": "charlie.wilson@example.com",
        "phone": "+1-555-0105",
        "address": "654 Maple Dr, Phoenix, AZ 85001",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 2,
        "customerCode": "C002",
        "fullName": "Jane Smith Update",
        "email": "jane.smith.upd@example.com",
        "phone": "+0123456789",
        "address": "Washington D.C",
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Partially Updated",
        "email": "john17.doe@example.com",
        "phone": "+9876543210",
        "address": null,
        "status": "ACTIVE",
        "createdAt": "2025-12-06T20:34:30"
    },
    {
        "id": 6,
        "customerCode": "C006",
        "fullName": "Trường Sơn Thạch",
        "email": "tst.hoc.upd@example.com",
        "phone": "+01012230290",
        "address": "Ho Chi Minh City",
        "status": "ACTIVE",
        "createdAt": "2025-12-13T00:20:47"
    }
]
```

### 12. Pagination with Sorting 
**GET** `/api/customers/paginated-sorted?page=0&size=5&sortBy=fullName&sortDir=asc`

**Response: 200 OK**
```json
{
    "totalItems": 5,
    "totalPages": 1,
    "pageSize": 5,
    "sortBy": "fullName",
    "customers": [
        {
            "id": 3,
            "customerCode": "C003",
            "fullName": "Bob Johnson",
            "email": "bob.johnson@example.com",
            "phone": "+1-555-0103",
            "address": "789 Pine Rd, Chicago, IL 60601",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 5,
            "customerCode": "C005",
            "fullName": "Charlie Wilson",
            "email": "charlie.wilson@example.com",
            "phone": "+1-555-0105",
            "address": "654 Maple Dr, Phoenix, AZ 85001",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 2,
            "customerCode": "C002",
            "fullName": "Jane Smith Update",
            "email": "jane.smith.upd@example.com",
            "phone": "+0123456789",
            "address": "Washington D.C",
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 1,
            "customerCode": "C001",
            "fullName": "John Partially Updated",
            "email": "john17.doe@example.com",
            "phone": "+9876543210",
            "address": null,
            "status": "ACTIVE",
            "createdAt": "2025-12-06T20:34:30"
        },
        {
            "id": 6,
            "customerCode": "C006",
            "fullName": "Trường Sơn Thạch",
            "email": "tst.hoc.upd@example.com",
            "phone": "+01012230290",
            "address": "Ho Chi Minh City",
            "status": "ACTIVE",
            "createdAt": "2025-12-13T00:20:47"
        }
    ],
    "currentPage": 0,
    "sortDir": "asc"
}
```






