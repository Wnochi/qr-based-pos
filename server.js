const express = require("express");
const app = express();

app.use(express.json());

// Import product routes
const productRoutes = require("./routes/product");
app.use("/products", productRoutes);

app.listen(3000, () => console.log("Server running on port 3000"));
