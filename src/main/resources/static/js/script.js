/*Product Infor*/

const products = [

    /* VEGETABLES */

    {
        id: "V001",
        name: "Fresh Carrot",
        category: "vegetables",
        categoryName: "Vegetables",
        price: 250,
        oldPrice: 300,
        rating: 4.8,
        badge: "FRESH",
        image:
            "/images/products/vegetables/carrot.jpg"
    },

    {
        id: "V002",
        name: "Fresh Tomato",
        category: "vegetables",
        categoryName: "Vegetables",
        price: 180,
        oldPrice: 220,
        rating: 4.7,
        badge: "POPULAR",
        image:
            "/images/products/vegetables/tomato.jpg"
    },

    {
        id: "V003",
        name: "Fresh Potato",
        category: "vegetables",
        categoryName: "Vegetables",
        price: 300,
        oldPrice: 350,
        rating: 4.6,
        badge: "VALUE",
        image:
            "/images/products/vegetables/potato.jpg"
    },

    {
        id: "V004",
        name: "Broccoli",
        category: "vegetables",
        categoryName: "Vegetables",
        price: 400,
        oldPrice: 450,
        rating: 4.9,
        badge: "FRESH",
        image:
            "/images/products/vegetables/broccoli.jpg"
    },


    /* FRUITS */

    {
        id: "F001",
        name: "Red Apple",
        category: "fruits",
        categoryName: "Fruits",
        price: 500,
        oldPrice: 600,
        rating: 4.9,
        badge: "BEST SELLER",
        image:
            "/images/products/fruits/apple.jpg"
    },

    {
        id: "F002",
        name: "Banana",
        category: "fruits",
        categoryName: "Fruits",
        price: 350,
        oldPrice: 400,
        rating: 4.7,
        badge: "FRESH",
        image:
            "/images/products/fruits/banana.jpg"
    },

    {
        id: "F003",
        name: "Orange",
        category: "fruits",
        categoryName: "Fruits",
        price: 450,
        oldPrice: 520,
        rating: 4.8,
        badge: "VITAMIN C",
        image:
            "/images/products/fruits/orange.jpg"
    },

    {
        id: "F004",
        name: "Grapes",
        category: "fruits",
        categoryName: "Fruits",
        price: 650,
        oldPrice: 750,
        rating: 4.9,
        badge: "PREMIUM",
        image:
            "/images/products/fruits/grapes.jpg"
    },


    /* DAIRY */

    {
        id: "D001",
        name: "Fresh Milk",
        category: "dairy",
        categoryName: "Dairy",
        price: 450,
        oldPrice: 500,
        rating: 4.9,
        badge: "BEST SELLER",
        image:
            "/images/products/dairy/milk.jpg"
    },

    {
        id: "D002",
        name: "Yogurt",
        category: "dairy",
        categoryName: "Dairy",
        price: 120,
        oldPrice: 150,
        rating: 4.6,
        badge: "FRESH",
        image:
            "/images/products/dairy/yogurt.jpg"
    },

    {
        id: "D003",
        name: "Cheese",
        category: "dairy",
        categoryName: "Dairy",
        price: 850,
        oldPrice: 950,
        rating: 4.8,
        badge: "PREMIUM",
        image:
            "/images/products/dairy/cheese.jpg"
    },

    {
        id: "D004",
        name: "Butter",
        category: "dairy",
        categoryName: "Dairy",
        price: 600,
        oldPrice: 680,
        rating: 4.7,
        badge: "POPULAR",
        image:
            "/images/products/dairy/butter.jpg"
    },


    /* SNACKS */

    {
        id: "S001",
        name: "Potato Chips",
        category: "snacks",
        categoryName: "Snacks",
        price: 350,
        oldPrice: 400,
        rating: 4.8,
        badge: "HOT",
        image:
            "/images/products/snacks/chips.jpg"
    },

    {
        id: "S002",
        name: "Biscuits",
        category: "snacks",
        categoryName: "Snacks",
        price: 220,
        oldPrice: 260,
        rating: 4.6,
        badge: "VALUE",
        image:
            "/images/products/snacks/biscuits.jpg"
    },

    {
        id: "S003",
        name: "Chocolate",
        category: "snacks",
        categoryName: "Snacks",
        price: 400,
        oldPrice: 480,
        rating: 4.9,
        badge: "BEST SELLER",
        image:
            "/images/products/snacks/chocolate.jpg"
    },

    {
        id: "S004",
        name: "Popcorn",
        category: "snacks",
        categoryName: "Snacks",
        price: 300,
        oldPrice: 350,
        rating: 4.7,
        badge: "POPULAR",
        image:
            "/images/products/snacks/popcorn.jpg"
    }
];



/*Real stock from springboot*/

let stockLevels = {};



async function loadStock() {

    try {

        const response =
            await fetch(
                "/api/stock"
            );


        if (!response.ok) {

            throw new Error(
                "Could not load stock."
            );
        }


        stockLevels =
            await response.json();


        renderProducts();


        setupCartImages();


        setupCartMaximumQuantity();


    }
    catch (error) {

        console.error(
            "Stock loading error:",
            error
        );


        /*
         * Still render products
         * even if API fails.
         */

        renderProducts();
    }
}



/*Product Image MAP*/

const productImageMap = {};


products.forEach(
    product => {

        productImageMap[
            product.id
            ] =
            product.image;

    }
);



/*Hero videos*/

const videos = [

    "/videos/grocery1.mp4",
    "/videos/grocery2.mp4",
    "/videos/grocery3.mp4",
    "/videos/grocery4.mp4"

];


let currentVideoIndex = 0;


const heroVideo =
    document.getElementById(
        "heroVideo"
    );



function playVideo() {

    if (!heroVideo) {
        return;
    }


    heroVideo.src =
        videos[
            currentVideoIndex
            ];


    heroVideo.muted =
        true;


    heroVideo.load();


    heroVideo
        .play()
        .catch(
            error => {

                console.log(
                    "Video error:",
                    error
                );

            }
        );
}



if (heroVideo) {

    playVideo();


    heroVideo.addEventListener(
        "ended",
        function () {

            currentVideoIndex++;


            if (
                currentVideoIndex
                >= videos.length
            ) {

                currentVideoIndex = 0;
            }


            playVideo();

        }
    );
}



/*Category Information*/

let activeCategory =
    "vegetables";


const categoryInfo = {

    vegetables: {

        title:
            "Fresh Vegetables",

        description:
            "Healthy and fresh vegetables for your daily meals."
    },


    fruits: {

        title:
            "Fresh Fruits",

        description:
            "Naturally fresh and delicious fruits."
    },


    dairy: {

        title:
            "Dairy Products",

        description:
            "Fresh dairy essentials for your family."
    },


    snacks: {

        title:
            "Tasty Snacks",

        description:
            "Delicious snacks for every moment."
    }
};



const productGrid =
    document.getElementById(
        "productGrid"
    );


const noProducts =
    document.getElementById(
        "noProducts"
    );


const searchInput =
    document.getElementById(
        "searchInput"
    );


const categoryHeading =
    document.getElementById(
        "categoryHeading"
    );


const categoryDescription =
    document.getElementById(
        "categoryDescription"
    );



/*Create product Cart*/

function createProductCard(
    product
) {

    const stock =
        stockLevels[
            product.id
            ] ?? 0;


    let stockText;


    let stockClass;


    if (stock === 0) {

        stockText =
            "Out of Stock";


        stockClass =
            "out-stock";

    }

    else if (stock <= 5) {

        stockText =
            `Only ${stock} left`;


        stockClass =
            "low-stock";

    }

    else {

        stockText =
            `${stock} in stock`;


        stockClass =
            "in-stock";

    }


    const disabled =
        stock === 0;


    return `

        <article class="product-card">


            <div class="product-image-wrapper">


                <img
                    class="product-photo"
                    src="${product.image}"
                    alt="${product.name}">


                <span class="product-badge">

                    ${product.badge}

                </span>


            </div>



            <div class="product-info">


                <span class="product-category">

                    ${product.categoryName}

                </span>



                <h3 class="product-title">

                    ${product.name}

                </h3>



                <div class="rating">

                    <span class="stars">

                        ★★★★★

                    </span>


                    <span>

                        ${product.rating}

                    </span>

                </div>



                <div class="stock-row ${stockClass}">


                    <span class="stock-dot">
                    </span>


                    ${stockText}


                </div>



                <div class="price-row">


                    <span class="product-price">

                        Rs. ${product.price.toFixed(2)}

                    </span>


                    <span class="old-price">

                        Rs. ${product.oldPrice.toFixed(2)}

                    </span>


                </div>



                <form

                    action="/cart/add"

                    method="post"

                    class="product-form add-cart-form"

                    data-product-name="${product.name}">


                    <input
                        type="hidden"
                        name="productId"
                        value="${product.id}">


                    <input
                        type="hidden"
                        name="productName"
                        value="${product.name}">


                    <input
                        type="hidden"
                        name="price"
                        value="${product.price}">



                    <div class="quantity-control">


                        <button
                            type="button"
                            class="qty-btn qty-minus"

                            ${disabled ? "disabled" : ""}>

                            −

                        </button>



                        <input
                            class="quantity-field"

                            type="number"

                            name="quantity"

                            value="${disabled ? 0 : 1}"

                            min="${disabled ? 0 : 1}"

                            max="${stock}"

                            ${disabled ? "disabled" : ""}>



                        <button
                            type="button"
                            class="qty-btn qty-plus"

                            ${disabled ? "disabled" : ""}>

                            +

                        </button>


                    </div>



                    <button

                        type="submit"

                        class="btn btn-primary add-cart-button"

                        ${disabled ? "disabled" : ""}>


                        ${
        disabled
            ? "Out of Stock"
            : "🛒 Add"
    }


                    </button>


                </form>


            </div>


        </article>

    `;
}



/*Render Product*/

function renderProducts() {

    if (!productGrid) {

        return;
    }


    const query =
        searchInput
            ?
            searchInput
                .value
                .trim()
                .toLowerCase()
            :
            "";


    let filteredProducts;


    /*
     * SEARCH MODE
     */

    if (query !== "") {

        filteredProducts =
            products.filter(
                product =>

                    product.name
                        .toLowerCase()
                        .includes(query)

                    ||

                    product.categoryName
                        .toLowerCase()
                        .includes(query)
            );


        if (categoryHeading) {

            categoryHeading
                .textContent =
                "Search Results";
        }


        if (categoryDescription) {

            categoryDescription
                .textContent =
                `Showing results for "${query}"`;
        }

    }

    /*
     * CATEGORY MODE
     */

    else {

        filteredProducts =
            products.filter(

                product =>

                    product.category
                    === activeCategory
            );


        if (categoryHeading) {

            categoryHeading
                .textContent =

                categoryInfo[
                    activeCategory
                    ].title;
        }


        if (categoryDescription) {

            categoryDescription
                .textContent =

                categoryInfo[
                    activeCategory
                    ].description;
        }
    }


    productGrid.innerHTML =

        filteredProducts

            .map(
                createProductCard
            )

            .join("");


    if (noProducts) {

        noProducts.style.display =

            filteredProducts.length === 0

                ? "block"

                : "none";
    }


    setupQuantityControls();


    setupAddCartForms();
}



/*Category Button*/

const categoryTabs =
    document.querySelectorAll(
        ".category-tab"
    );


categoryTabs.forEach(

    tab => {

        tab.addEventListener(
            "click",

            function () {

                activeCategory =
                    this.dataset.category;


                categoryTabs.forEach(

                    button => {

                        button
                            .classList
                            .remove(
                                "active"
                            );

                    }
                );


                this
                    .classList
                    .add(
                        "active"
                    );


                if (searchInput) {

                    searchInput.value =
                        "";
                }


                renderProducts();

            }
        );

    }
);



/*Search*/

if (searchInput) {

    const urlParameters =
        new URLSearchParams(
            window.location.search
        );


    const searchFromUrl =
        urlParameters.get(
            "q"
        );


    if (searchFromUrl) {

        searchInput.value =
            searchFromUrl;
    }


    searchInput.addEventListener(

        "input",

        renderProducts
    );
}



/*Quantity Button*/

function setupQuantityControls() {

    const plusButtons =
        document.querySelectorAll(
            ".qty-plus"
        );


    const minusButtons =
        document.querySelectorAll(
            ".qty-minus"
        );


    plusButtons.forEach(

        button => {

            button.onclick =
                function () {

                    const control =
                        this.closest(
                            ".quantity-control"
                        );


                    if (!control) {

                        return;
                    }


                    const input =
                        control.querySelector(
                            ".quantity-field"
                        );


                    if (!input) {

                        return;
                    }


                    let current =
                        Number(
                            input.value
                        );


                    const maximum =
                        Number(
                            input.max
                        );


                    if (
                        !maximum
                        ||
                        current < maximum
                    ) {

                        input.value =
                            current + 1;
                    }

                };

        }
    );


    minusButtons.forEach(

        button => {

            button.onclick =
                function () {

                    const control =
                        this.closest(
                            ".quantity-control"
                        );


                    if (!control) {

                        return;
                    }


                    const input =
                        control.querySelector(
                            ".quantity-field"
                        );


                    if (!input) {

                        return;
                    }


                    const current =
                        Number(
                            input.value
                        );


                    if (current > 1) {

                        input.value =
                            current - 1;
                    }

                };

        }
    );
}



/*Add to cart message*/

function setupAddCartForms() {

    const forms =
        document.querySelectorAll(
            ".add-cart-form"
        );


    forms.forEach(

        form => {

            form.addEventListener(

                "submit",

                function () {

                    const productName =
                        this.dataset.productName;


                    localStorage.setItem(

                        "freshCartToast",

                        `${productName} added to your cart`
                    );

                }
            );

        }
    );
}



/*TOAST*/

function showToast(
    message
) {

    const toast =
        document.getElementById(
            "toast"
        );


    const toastMessage =
        document.getElementById(
            "toastMessage"
        );


    if (
        !toast
        ||
        !toastMessage
    ) {

        return;
    }


    toastMessage.textContent =
        message;


    toast
        .classList
        .add(
            "show"
        );


    setTimeout(

        function () {

            toast
                .classList
                .remove(
                    "show"
                );

        },

        3000
    );
}



const storedToast =
    localStorage.getItem(
        "freshCartToast"
    );


if (storedToast) {

    showToast(
        storedToast
    );


    localStorage.removeItem(
        "freshCartToast"
    );
}



/*Cart product Image*/

function setupCartImages() {

    const cartImages =
        document.querySelectorAll(
            ".product-auto-image"
        );


    cartImages.forEach(

        image => {

            const productId =
                image.dataset.productId;


            if (
                productImageMap[
                    productId
                    ]
            ) {

                image.src =
                    productImageMap[
                        productId
                        ];
            }

        }
    );
}



/*Cart MAX quantity*/

function setupCartMaximumQuantity() {

    const quantityInputs =
        document.querySelectorAll(
            ".cart-item .quantity-field"
        );


    quantityInputs.forEach(

        input => {

            const cartItem =
                input.closest(
                    ".cart-item"
                );


            if (!cartItem) {

                return;
            }


            const image =
                cartItem.querySelector(
                    ".product-auto-image"
                );


            if (!image) {

                return;
            }


            const productId =
                image.dataset.productId;


            const stock =
                stockLevels[
                    productId
                    ];


            if (
                stock !== undefined
            ) {

                input.max =
                    stock;
            }

        }
    );
}



/*Order Progress*/

const orderProgressElements =
    document.querySelectorAll(
        ".order-progress"
    );


const progressStatuses = [

    "PENDING",
    "CONFIRMED",
    "PROCESSING",
    "COMPLETED"
];


const progressNames = [

    "Placed",
    "Confirmed",
    "Processing",
    "Completed"
];


orderProgressElements.forEach(

    progress => {

        const status =
            progress.dataset.status;


        /*
         * CANCELLED ORDER
         */

        if (
            status === "CANCELLED"
        ) {

            progress.innerHTML = `

                <div class="cancelled-progress">

                    ✕ Order Cancelled

                </div>

            `;


            return;
        }


        const currentIndex =
            progressStatuses.indexOf(
                status
            );


        let html =
            '<div class="progress-track">';


        progressNames.forEach(

            (name, index) => {

                const completed =
                    index <= currentIndex;


                html += `

                    <div class="progress-step ${completed ? "complete" : ""}">

                        <div class="progress-circle">

                            ${
                    completed
                        ? "✓"
                        : index + 1
                }

                        </div>

                        <span>

                            ${name}

                        </span>

                    </div>

                `;


                if (
                    index
                    <
                    progressNames.length - 1
                ) {

                    html += `

                        <div class="progress-line ${index < currentIndex ? "complete" : ""}">
                        </div>

                    `;
                }

            }
        );


        html +=
            "</div>";


        progress.innerHTML =
            html;

    }
);



/*Confirm Remove*/

function confirmRemove() {

    return confirm(
        "Remove this product from your cart?"
    );
}



/*  Confirm Delete*/

function confirmDelete() {

    return confirm(
        "Are you sure you want to permanently delete this order?"
    );
}



/*Start Website*/

loadStock();


setupQuantityControls();


setupCartImages();
