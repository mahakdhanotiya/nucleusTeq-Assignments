/*--PRODUCT DATA--*/

let productList = [
    // Electronics
    { id: 1, name: "Laptop", price: 50000, category: "electronics", stock: 10 },
    { id: 2, name: "Mobile", price: 30000, category: "electronics", stock: 5 },
    { id: 3, name: "Headphones", price: 2000, category: "electronics", stock: 3 },

    // Clothing
    { id: 4, name: "T-Shirt", price: 800, category: "clothing", stock: 25 },
    { id: 5, name: "Jeans", price: 1500, category: "clothing", stock: 8 },
    { id: 6, name: "Jacket", price: 2500, category: "clothing", stock: 2 },

    // Books
    { id: 7, name: "Book", price: 500, category: "books", stock: 5 },
    { id: 8, name: "Notebook", price: 200, category: "books", stock: 0 },
    { id: 9, name: "Story Book", price: 350, category: "books", stock: 7 },

    // Accessories
    { id: 10, name: "Watch", price: 1500, category: "accessories", stock: 0 },
    { id: 11, name: "Sunglasses", price: 1200, category: "accessories", stock: 6 },
    { id: 12, name: "Bag", price: 1800, category: "accessories", stock: 4 }
];

/*--RENDER PRODUCTS--*/

function renderProducts(products) {

    let container = document.getElementById("productGrid");

    container.innerHTML = "";

    if (products.length === 0) {
        container.innerHTML = "<p>No products found</p>";
        return;
    }

    products.forEach(function(product) {

        let card = document.createElement("div");
        card.className = "product-card";

        card.innerHTML = `
            <h3>${product.name}</h3>
            <p>Category: ${product.category}</p>
            <p>Price: ₹${product.price}</p>
            <p>Stock: ${product.stock}</p>
            <button onclick="deleteProduct(${product.id})">Delete</button>
        `;

        container.appendChild(card);
    });
}

/*---LOCAL STORAGE SETUP---*/


let storedProducts = JSON.parse(localStorage.getItem("products"));

if (!storedProducts) {

    localStorage.setItem("products", JSON.stringify(productList));
    
    storedProducts = productList;
}

//---ANALYTICS FUNCTION----//

function renderAnalytics(products) {

    let container = document.getElementById("analyticsContainer");

    // total products
    let totalProducts = products.length;

    // total inventory value
    let totalValue = 0;
    products.forEach(function(product) {
        totalValue += product.price * product.stock;
    });

    // out of stock count
    let outOfStock = products.filter(function(product) {
        return product.stock === 0;
    }).length;

    // UI
    container.innerHTML = `
        <div class="analytics-card">
            <h3>Total Products</h3>
            <p>${totalProducts}</p>
        </div>

        <div class="analytics-card">
            <h3>Total Inventory Value</h3>
            <p>₹${totalValue}</p>
        </div>

        <div class="analytics-card">
            <h3>Out of Stock</h3>
            <p>${outOfStock}</p>
        </div>
    `;
}

renderProducts(storedProducts);
renderAnalytics(storedProducts);


document.getElementById("productForm").addEventListener("submit", function(e) {

    e.preventDefault();

    // Get input values
    let name = document.getElementById("name").value;
    let price = document.getElementById("price").value;
    let stock = document.getElementById("stock").value;
    let category = document.getElementById("category").value;

    // Validation
    if (name === "" || price <= 0 || stock < 0 || category === "") {
        alert("Please fill all fields correctly");
        return;
    }

    // Get existing products from localStorage
    let products = JSON.parse(localStorage.getItem("products"));

    // Create new product object
    let newProduct = {
        id: Date.now(),
        name: name,
        price: Number(price),
        stock: Number(stock),
        category: category
    };

    // Add new product to array
    products.push(newProduct);

    // Save updated products to localStorage
    localStorage.setItem("products", JSON.stringify(products));

    // Re-render UI
    renderProducts(products);
    renderAnalytics(products);

    // Reset form
    document.getElementById("productForm").reset();
});

function deleteProduct(id) {

    // Retrieving existing products from localStorage
    let products = JSON.parse(localStorage.getItem("products"));

    // Remove selected product
    products = products.filter(function(product) {
        return product.id !== id;
    });

    // Save Updated productlist to localStorage
    localStorage.setItem("products", JSON.stringify(products));

    // Update UI
    renderProducts(products);
    renderAnalytics(products);
}
