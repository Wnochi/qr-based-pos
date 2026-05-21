# QR-Based POS System — Project Plan

## Goal
Build a QR-based Point-of-Sale (POS) system for a sole proprietorship business that:
- Digitalizes products with QR codes.
- Allows cashier transactions via QR scanning.
- Automates payment computation and receipt generation.
- Updates inventory stock levels automatically.
- Generates sales reports for admin analysis.

## Functional Modules

### 1. Product Digitalization (Admin)
- Input product details: name, price, stock, threshold, image.
- Validate required fields.
- Generate unique QR code linked to product ID.
- Save product record in database.
- Display inventory masterlist.

### 2. Transaction (Cashier)
- Scan QR code → identify product.
- Retrieve product details (name, price).
- Display transaction info (subtotal, items).

### 3. Payment
- Compute total amount due.
- Input cash received → calculate change.
- Store transaction breakdown (items, quantity, subtotal).
- Store transaction overview (total, cash received, change).
- Generate physical/digital receipt.

### 4. Inventory Update
- Deduct purchased quantity from stock.
- Update stock levels.
- Monitor thresholds → trigger low-stock alert.
- Refresh inventory masterlist.

### 5. Sales Report (Admin)
- Admin selects month/year.
- Fetch transaction history.
- Generate daily, monthly, yearly reports.
- Display top products, revenue, items sold.

## Database Schema (MariaDB)

**Products Table**
- id (QR code ID, PK)
- name
- price
- stock
- threshold
- imagePath
- qrCodePath

**Transactions Table**
- id (PK)
- time
- total
- cashReceived
- change

**TransactionDetails Table**
- id (PK)
- transactionId (FK)
- productId (FK)
- quantity
- subtotal

**PaymentDetails Table**
- transactionId (FK)
- breakdown (JSON or relational)

**TransactionArchive Table**
- transactionId (FK)
- overview (total, cash, change)

## Spring Boot Code Structure

### Entities (simple, clear)
```java
@Entity
public class Product { String id; String name; BigDecimal price; int stock; int threshold; String imagePath; String qrCodePath; }

@Entity
public class Transaction { @Id @GeneratedValue Long id; LocalDateTime time; BigDecimal total; BigDecimal cashReceived; BigDecimal change; }

@Entity
public class TransactionDetail { @Id @GeneratedValue Long id; Long transactionId; String productId; int quantity; double subtotal; }
```

### Repositories
- `ProductRepository extends JpaRepository<Product, String>`
- `TransactionRepository extends JpaRepository<Transaction, Long>`
- `TransactionDetailRepository extends JpaRepository<TransactionDetail, Long>`

### Controllers (minimal endpoints)
- **AdminController**
  - `POST /products` → add product
  - `POST /products/{id}/qr` → generate QR
  - `GET /inventory` → view masterlist
- **CashierController**
  - `POST /transaction/scan` → decode QR, fetch product
  - `POST /transaction/add` → add product to transaction
  - `POST /transaction/pay` → finalize payment, generate receipt
- **ReportController**
  - `GET /reports/monthly?month=5&year=2026`
  - `GET /reports/yearly?year=2026`

### Services
- `QRService` → generate/scan QR codes (ZXing).
- `InventoryService` → update stock, check thresholds.
- `PaymentService` → compute totals, store payment records.
- `ReportService` → aggregate transactions for reports.

## Domain Model Notes
- Use `VARCHAR(36)` or UUID for `Product.id`.
- Use `DECIMAL(13,2)` for money fields.
- FK constraints between `transaction_details` and `transactions`/`products`.
- Index `transactions.time` for reporting.

## Validation & Business Rules
- Deduct stock atomically during transaction finalization.
- Fail payment if insufficient stock.
- Mark low-stock when `stock <= threshold`.
- Compute transaction `total` from `TransactionDetail.subtotal`.

## Roadmap
1. Setup Spring Boot project with dependencies (Web, JPA, MariaDB).
2. Implement Product entity + QR generation → test adding products.
3. Build Transaction + Payment flow → test scanning QR and computing totals.
4. Add Inventory update logic → auto deduct stock after payment.
5. Implement Sales Report module → generate monthly/yearly reports.

## Implementation Guidelines
- Keep code simple and well-named for readability.
- Prefer small services with single responsibility.
- Write unit tests for services and integration tests for controllers.
- Provide a small sample dataset and README with run steps.

---
Generated for quick reference and implementation guidance.
