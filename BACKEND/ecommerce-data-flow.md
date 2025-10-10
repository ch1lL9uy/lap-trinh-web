# 🛍️ VÍ DỤ THỰC TẾ - FLOW XỬ LÝ DỮ LIỆU E-COMMERCE

## 📦 CASE 1: QUẢN LÝ SẢN PHẨM

### Bước 1: Admin tạo Category
```java
// Tạo category cha
Category namCategory = Category.builder()
    .categoryName("Thời trang Nam")
    .slug("thoi-trang-nam")
    .sortOrder(1)
    .isActive(true)
    .parentCategory(null)  // Category gốc
    .build();

// Tạo subcategory
Category aoNamCategory = Category.builder()
    .categoryName("Áo Nam")
    .slug("ao-nam")
    .sortOrder(1)
    .parentCategory(namCategory)  // Mapping với category cha
    .build();

Category aoThunNam = Category.builder()
    .categoryName("Áo Thun Nam")
    .slug("ao-thun-nam")
    .sortOrder(1)
    .parentCategory(aoNamCategory)  // Cấp 3
    .build();
```

**Cách hiển thị trên web:**
```
Thời trang Nam (namCategory)
  └── Áo Nam (aoNamCategory)
      ├── Áo Thun Nam (aoThunNam)
      ├── Áo Sơ Mi Nam
      └── Áo Polo Nam
```

### Bước 2: Admin tạo Product (Sản phẩm tổng quát)
```java
Product aoThunBasic = Product.builder()
    .productName("Áo Thun Nam Basic Cotton")
    .description("Chất liệu cotton 100%, form regular fit, phù hợp mặc hằng ngày")
    .category(aoThunNam)  // Mapping với Category
    .isActive(true)
    .isFeatured(true)
    .build();
```

### Bước 3: Admin upload ảnh cho Product
```java
ProductImage img1 = ProductImage.builder()
    .imageUrl("https://cdn.shop.vn/ao-thun-basic-main.jpg")
    .product(aoThunBasic)  // Mapping với Product
    .isPrimary(true)  // Ảnh chính
    .sortOrder(1)
    .build();

ProductImage img2 = ProductImage.builder()
    .imageUrl("https://cdn.shop.vn/ao-thun-basic-detail.jpg")
    .product(aoThunBasic)
    .isPrimary(false)
    .sortOrder(2)
    .build();

aoThunBasic.setImages(Arrays.asList(img1, img2));
```

### Bước 4: Admin tạo ProductItem (Biến thể cụ thể)
```java
// Biến thể 1: Size M - Màu Đen
ProductItem item1 = ProductItem.builder()
    .sku("ATB-2024-M-BLACK")
    .product(aoThunBasic)  // Mapping với Product cha
    .size(Size.M)
    .color("Đen")
    .price(new BigDecimal("199000"))
    .stockQuantity(100)
    .build();

// Biến thể 2: Size M - Màu Trắng
ProductItem item2 = ProductItem.builder()
    .sku("ATB-2024-M-WHITE")
    .product(aoThunBasic)
    .size(Size.M)
    .color("Trắng")
    .price(new BigDecimal("199000"))
    .stockQuantity(80)
    .build();

// Biến thể 3: Size L - Màu Đen
ProductItem item3 = ProductItem.builder()
    .sku("ATB-2024-L-BLACK")
    .product(aoThunBasic)
    .size(Size.L)
    .color("Đen")
    .price(new BigDecimal("209000"))  // Size L đắt hơn 10k
    .stockQuantity(75)
    .build();

// Biến thể 4: Size XL - Màu Đỏ
ProductItem item4 = ProductItem.builder()
    .sku("ATB-2024-XL-RED")
    .product(aoThunBasic)
    .size(Size.XL)
    .color("Đỏ")
    .price(new BigDecimal("219000"))
    .stockQuantity(50)
    .build();

aoThunBasic.setProductItems(Arrays.asList(item1, item2, item3, item4));
```

**Cách hiển thị trên web:**
```
Áo Thun Nam Basic Cotton - 199,000đ - 219,000đ

Màu sắc: [Đen] [Trắng] [Đỏ]
Size:    [M] [L] [XL]

Khi chọn: Đen + Size M
→ Hiển thị: 199,000đ | Còn 100 sản phẩm | SKU: ATB-2024-M-BLACK
```

---

## 🛒 CASE 2: KHÁCH HÀNG MUA HÀNG

### Bước 1: User đăng ký tài khoản
```java
User newUser = User.builder()
    .username("nguyenvana")
    .email("nguyenvana@gmail.com")
    .password(passwordEncoder.encode("123456"))
    .role(Role.CUSTOMER)
    .isActive(true)
    .build();

// Tự động tạo UserInfo
UserInfo userInfo = UserInfo.builder()
    .user(newUser)  // Mapping OneToOne với User
    .firstName("Văn A")
    .lastName("Nguyễn")
    .phone("0901234567")
    .dob(LocalDate.of(1995, 5, 15))
    .build();

newUser.setUserInfo(userInfo);

// Tự động tạo Cart khi user đăng ký
Cart cart = Cart.builder()
    .user(newUser)  // Mapping OneToOne với User
    .build();
```

### Bước 2: User thêm địa chỉ giao hàng
```java
Address homeAddress = Address.builder()
    .user(newUser)  // Mapping với User
    .recipientName("Nguyễn Văn A")
    .phone("0901234567")
    .address("123 Nguyễn Huệ")
    .ward("Phường Bến Nghé")
    .district("Quận 1")
    .province("Hồ Chí Minh")
    .isDefault(true)  // Địa chỉ mặc định
    .build();

Address officeAddress = Address.builder()
    .user(newUser)
    .recipientName("Nguyễn Văn A")
    .phone("0901234567")
    .address("456 Lê Lợi")
    .ward("Phường Bến Thành")
    .district("Quận 1")
    .province("Hồ Chí Minh")
    .isDefault(false)
    .build();

newUser.setAddresses(Arrays.asList(homeAddress, officeAddress));
```

### Bước 3: User browse sản phẩm và thêm vào giỏ
```java
// User chọn: Áo Thun Basic - Size M - Màu Đen - Số lượng 2
CartItem cartItem1 = CartItem.builder()
    .cart(cart)  // Mapping với Cart của user
    .productItem(item1)  // SKU: ATB-2024-M-BLACK
    .quantity(2)
    .build();

// User chọn thêm: Áo Thun Basic - Size L - Màu Trắng - Số lượng 1
CartItem cartItem2 = CartItem.builder()
    .cart(cart)
    .productItem(item2)  // SKU khác
    .quantity(1)
    .build();

cart.setCartItems(Arrays.asList(cartItem1, cartItem2));
```

**Hiển thị giỏ hàng:**
```
Giỏ hàng của bạn:
┌────────────────────────────────────────────────────────────┐
│ Áo Thun Nam Basic Cotton                                   │
│ Size M - Màu Đen                                           │
│ 199,000đ x 2 = 398,000đ                    [+] [-] [Xóa]   │
├────────────────────────────────────────────────────────────┤
│ Áo Thun Nam Basic Cotton                                   │
│ Size L - Màu Trắng                                         │
│ 209,000đ x 1 = 209,000đ                    [+] [-] [Xóa]   │
└────────────────────────────────────────────────────────────┘
Tạm tính: 607,000đ
```

### Bước 4: User checkout - Tạo Order
```java
// Tính toán
BigDecimal subtotal = new BigDecimal("607000");
BigDecimal shippingFee = new BigDecimal("30000");
BigDecimal total = subtotal.add(shippingFee);

Order order = Order.builder()
    .user(newUser)  // Mapping với User
    .orderNumber("ORD-2024-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
    .orderDate(LocalDateTime.now())
    .status(OrderStatus.PENDING)
    .subtotal(subtotal)
    .shippingFee(shippingFee)
    .total(total)
    .currency("VND")
    .shippingAddress(homeAddress)  // Mapping với Address đã chọn
    .build();

// Chuyển CartItem → OrderItem
OrderItem orderItem1 = OrderItem.builder()
    .order(order)
    .productItem(item1)  // Reference đến ProductItem
    .quantity(2)
    .unitPrice(199000.0)  // Lưu giá tại thời điểm mua
    .totalPrice(398000.0)
    .build();

OrderItem orderItem2 = OrderItem.builder()
    .order(order)
    .productItem(item2)
    .quantity(1)
    .unitPrice(209000.0)
    .totalPrice(209000.0)
    .build();

order.setItems(Arrays.asList(orderItem1, orderItem2));

// Trừ tồn kho
item1.setStockQuantity(item1.getStockQuantity() - 2);  // 100 → 98
item2.setStockQuantity(item2.getStockQuantity() - 1);  // 80 → 79

// Xóa giỏ hàng sau khi đặt
cart.getCartItems().clear();
```

### Bước 5: User thanh toán
```java
Payment payment = Payment.builder()
    .order(order)  // Mapping với Order
    .amount(total)
    .currency("VND")
    .method(PaymentMethod.VNPAY)
    .status(PaymentStatus.PENDING)
    .externalRef("VNPAY-TXN-123456789")  // Mã giao dịch từ VNPay
    .build();

order.setPayments(Arrays.asList(payment));

// Sau khi VNPay callback success
payment.setStatus(PaymentStatus.SUCCESS);
payment.setPaidAt(LocalDateTime.now());
order.setStatus(OrderStatus.PROCESSING);
```

**Email xác nhận:**
```
Đơn hàng #ORD-2024-A3F5B7C1 đã được xác nhận!

Chi tiết đơn hàng:
- Áo Thun Nam Basic Cotton (M - Đen) x2: 398,000đ
- Áo Thun Nam Basic Cotton (L - Trắng) x1: 209,000đ
Phí vận chuyển: 30,000đ
Tổng cộng: 637,000đ

Giao đến:
Nguyễn Văn A - 0901234567
123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, Hồ Chí Minh

Thanh toán: VNPay - Đã thanh toán
```

---

## ⭐ CASE 3: USER REVIEW SAU KHI NHẬN HÀNG

### Sau 3 ngày, Order status = SUCCESS
```java
order.setStatus(OrderStatus.SUCCESS);
```

### User viết review cho OrderItem
```java
// Review cho item 1: Áo Size M Màu Đen
Review review1 = Review.builder()
    .user(newUser)  // Người viết review
    .orderItem(orderItem1)  // Review cho OrderItem cụ thể
    .rating(5)
    .comment("Áo đẹp, chất liệu mát, đúng size. Sẽ mua lại!")
    .build();

// Review cho item 2: Áo Size L Màu Trắng
Review review2 = Review.builder()
    .user(newUser)
    .orderItem(orderItem2)
    .rating(4)
    .comment("Áo ok nhưng hơi rộng so với size L thường")
    .build();
```

**Kiểm tra logic:**
- ✅ User chỉ review được OrderItem của mình
- ✅ Mỗi OrderItem chỉ review 1 lần
- ✅ Review gắn với User (có thể query "các review của user X")
- ✅ Review gắn với OrderItem → biết được đánh giá cho size/màu cụ thể nào

---

## 🔍 CASE 4: QUERY DỮ LIỆU THỰC TẾ

### Query 1: Lấy tất cả sản phẩm trong Category "Áo Thun Nam"
```java
// Sử dụng mapping Product → Category
List<Product> products = productRepository.findByCategoryCategoryId(aoThunNam.getCategoryId());

// Hoặc từ Category
List<Product> products = aoThunNam.getProducts();
```

### Query 2: Lấy tất cả biến thể của 1 Product
```java
// Sử dụng mapping Product → ProductItem
Product product = productRepository.findById(productId);
List<ProductItem> variants = product.getProductItems();

// In ra:
// SKU: ATB-2024-M-BLACK | Size M | Màu Đen | 199,000đ | Tồn: 98
// SKU: ATB-2024-M-WHITE | Size M | Màu Trắng | 199,000đ | Tồn: 79
// SKU: ATB-2024-L-BLACK | Size L | Màu Đen | 209,000đ | Tồn: 75
// SKU: ATB-2024-XL-RED | Size XL | Màu Đỏ | 219,000đ | Tồn: 50
```

### Query 3: Lấy giỏ hàng của User
```java
// Sử dụng mapping User → Cart → CartItem → ProductItem
User user = userRepository.findByUsername("nguyenvana");
Cart cart = user.getCart(); // OneToOne
List<CartItem> cartItems = cart.getCartItems();

for (CartItem item : cartItems) {
    ProductItem productItem = item.getProductItem();
    Product product = productItem.getProduct();
    
    System.out.println(
        product.getProductName() + 
        " - Size " + productItem.getSize() + 
        " - Màu " + productItem.getColor() +
        " x " + item.getQuantity() +
        " = " + (productItem.getPrice().multiply(new BigDecimal(item.getQuantity())))
    );
}
```

### Query 4: Lấy tất cả đơn hàng của User
```java
// Sử dụng mapping User → Order
User user = userRepository.findByUsername("nguyenvana");
List<Order> orders = user.getOrders();

for (Order order : orders) {
    System.out.println("Đơn hàng: " + order.getOrderNumber());
    System.out.println("Trạng thái: " + order.getStatus());
    System.out.println("Tổng tiền: " + order.getTotal());
    
    // Lấy chi tiết items
    for (OrderItem item : order.getItems()) {
        System.out.println("  - " + item.getProductItem().getProduct().getProductName());
    }
}
```

### Query 5: Lấy địa chỉ mặc định của User
```java
User user = userRepository.findByUsername("nguyenvana");
Address defaultAddress = user.getAddresses().stream()
    .filter(Address::getIsDefault)
    .findFirst()
    .orElse(null);

String fullAddress = defaultAddress.getFullAddress();
// Output: "123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, Hồ Chí Minh"
```

### Query 6: Lấy tất cả review của 1 Product
```java
// Phức tạp hơn: Product → ProductItem → OrderItem → Review
Product product = productRepository.findById(productId);
List<Review> allReviews = new ArrayList<>();

for (ProductItem item : product.getProductItems()) {
    for (CartItem cartItem : item.getCartItems()) {
        // CartItem không có review, phải qua OrderItem
    }
}

// Cách tốt hơn: Query trực tiếp
@Query("SELECT r FROM Review r WHERE r.orderItem.productItem.product.productId = :productId")
List<Review> findReviewsByProductId(@Param("productId") String productId);
```

### Query 7: Báo cáo doanh thu theo Category
```java
@Query("""
    SELECT c.categoryName, SUM(oi.totalPrice)
    FROM OrderItem oi
    JOIN oi.productItem pi
    JOIN pi.product p
    JOIN p.category c
    WHERE oi.order.status = 'SUCCESS'
    GROUP BY c.categoryName
""")
List<Object[]> getRevenueByCategoryReport();
```

---

## 🎯 TÓM TẮT CÁCH MAPPING HOẠT ĐỘNG

### 1. **OneToOne** - Quan hệ 1-1
```java
User ←→ UserInfo
User ←→ Cart

// Truy cập:
user.getUserInfo().getFirstName()
cart.getUser().getUsername()
```

### 2. **OneToMany / ManyToOne** - Quan hệ 1-nhiều
```java
User → Address (1 user có nhiều address)
Product → ProductItem (1 product có nhiều variant)
Order → OrderItem (1 order có nhiều item)

// Truy cập:
user.getAddresses().get(0).getFullAddress()
product.getProductItems().forEach(...)
order.getItems().size()
```

### 3. **ManyToOne / OneToMany** - Chiều ngược lại
```java
Address → User
ProductItem → Product
OrderItem → Order

// Truy cập:
address.getUser().getEmail()
productItem.getProduct().getProductName()
orderItem.getOrder().getOrderNumber()
```

### 4. **Self-referencing** - Tự tham chiếu
```java
Category → parentCategory (Category)
Category → subCategories (List<Category>)

// Truy cập:
aoThunNam.getParentCategory().getCategoryName() // "Áo Nam"
namCategory.getSubCategories().forEach(...) // Duyệt các category con
```

### 5. **Lazy Loading** - Tải dữ liệu khi cần
```java
@ManyToOne(fetch = FetchType.LAZY)

// Không query ngay khi load User
User user = userRepository.findById(userId);

// Chỉ query khi truy cập
user.getOrders(); // ← Lúc này mới query orders
```

---

## 💡 LƯU Ý KHI LÀM VIỆC VỚI MAPPING

### ⚠️ N+1 Query Problem
```java
// BAD - Gây N+1 queries
List<Order> orders = orderRepository.findAll();
for (Order order : orders) {
    order.getUser().getUsername(); // Query thêm cho mỗi order
}

// GOOD - Dùng JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.user")
List<Order> findAllWithUser();
```

### ⚠️ Cascade Operations
```java
// Khi delete User, tự động delete Cart, Address, Order
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)

// Cẩn thận với CascadeType.REMOVE
```

### ⚠️ Orphan Removal
```java
// Khi remove CartItem khỏi Cart, tự động delete CartItem
@OneToMany(mappedBy = "cart", orphanRemoval = true)

cart.getCartItems().remove(cartItem); // ← cartItem bị xóa khỏi DB
```

### ✅ Best Practices
1. **Luôn dùng DTO cho response** - Tránh expose entity trực tiếp
2. **Dùng Projection cho query lớn** - Chỉ lấy field cần thiết
3. **Set mappedBy đúng** - Tránh tạo bảng join không cần thiết
4. **Index các foreign key** - Tăng tốc độ query
5. **Validate business logic** - Không chỉ dựa vào constraint DB