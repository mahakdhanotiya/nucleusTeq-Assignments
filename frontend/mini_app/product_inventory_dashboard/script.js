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

    if (!products || products.length === 0) {
        container.innerHTML = `<p style="text-align:center; font-weight:bold; padding:20px;">
        No products found
        </p>
        `;
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


let storedProducts = JSON.parse(localStorage.getItem("products")) || [];

if (!storedProducts) {

    localStorage.setItem("products", JSON.stringify(productList));
    
    storedProducts = productList;
}

//---ANALYTICS FUNCTION----//

function renderAnalytics(products) {

    let container = document.getElementById("analyticsContainer");

    // total products
    let totalProducts = products.length;

    // Calculate total inventory value
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


/*--ADD PRODUCT--*/

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
    let products = JSON.parse(localStorage.getItem("products")) || [];

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

/*---DELETE PRODUCT---*/

function deleteProduct(id) {

    // Ask for confirmation
    let isConfirmed = confirm("Are you sure you want to delete this product?");

    // If user clicks Cancel
    if (!isConfirmed) {
        return;
    }

    // Retrieve existing products from localStorage
    let products = JSON.parse(localStorage.getItem("products")) || [];

    // Remove selected product
    products = products.filter(function(product) {
        return product.id !== id;
    });

    // Save updated list
    localStorage.setItem("products", JSON.stringify(products));

    // Update UI
    renderProducts(products);
    renderAnalytics(products);
}

document.addEventListener("DOMContentLoaded", function() {

    /*---SEARCH FUNCTIONALITY---*/


    document.getElementById("searchInput").addEventListener("input", function() {

     // Get search input (case-insensitive)   
    let searchValue = this.value.toLowerCase();

    // Retrieve products from localStorage
    let products = JSON.parse(localStorage.getItem("products")) || [];

    // Filter products by name
    let filteredProducts = products.filter(function(product) {
        return product.name.toLowerCase().includes(searchValue);
    });

    //Update UI
    renderProducts(filteredProducts);
    renderAnalytics(filteredProducts);
});
});

/*---CATEGORY FILTER---*/

document.getElementById("categoryFilter").addEventListener("change", function() {

    // Get selected category
    let selectedCategory = this.value;

    // Retrieve products from localStorage
    let products = JSON.parse(localStorage.getItem("products")) || [];

    let filteredProducts;

    // Show all or filter by category
    if (selectedCategory === "all") {
        filteredProducts = products;
    } else {
        filteredProducts = products.filter(function(product) {
            return product.category === selectedCategory;
        });
    }

    // Update UI
    renderProducts(filteredProducts);
    renderAnalytics(filteredProducts);
});

/*---LOW STOCK FILTER---*/

document.getElementById("lowStockFilter").addEventListener("change", function() {

    // Check if filter is enabled
    let isChecked = this.checked;

    // Retrieve products from localStorage
    let products = JSON.parse(localStorage.getItem("products")) || [];

    let filteredProducts;

    // Filter products with stock less than 5
    if (isChecked) {
        filteredProducts = products.filter(function(product) {
            return product.stock < 5;
        });
    } else {
        filteredProducts = products;
    }

    // Update UI
    renderProducts(filteredProducts);
    renderAnalytics(filteredProducts);
});

/*---SORTING FUNCTIONALITY---*/

document.getElementById("sortOption").addEventListener("change", function() {

    // Get selected sorting option
    let option = this.value;

    // Retrieve products from localStorage
    let products = JSON.parse(localStorage.getItem("products")) || [];

    // Apply sorting based on selected option
    if (option === "low-high") {
        products.sort((a, b) => a.price - b.price);
    } 
    else if (option === "high-low") {
        products.sort((a, b) => b.price - a.price);
    } 
    else if (option === "a-z") {
        products.sort((a, b) => a.name.localeCompare(b.name));
    } 
    else if (option === "z-a") {
        products.sort((a, b) => b.name.localeCompare(a.name));
    }

    // Update UI
    renderProducts(products);
    renderAnalytics(products);
});

/*---FETCH PRODUCTS---*/

function fetchProducts() {
    return new Promise(function(resolve) {
        setTimeout(function() {
            let products = JSON.parse(localStorage.getItem("products")) || [];
            resolve(products);
        }, 2000);
    });
}

// Select all controls (search, filters, sorting)
let controls = document.querySelectorAll("#controls input, #controls select");

// Disable controls before loading
controls.forEach(control => control.disabled = true);

let loadingText = document.getElementById("loading");

fetchProducts().then(function(products) {

    // Hide loader
    loadingText.style.display = "none";

    // Enable controls after loading
    controls.forEach(control => control.disabled = false);

    renderProducts(products);
    renderAnalytics(products);
});

