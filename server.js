const express = require("express");
const app = express();

app.use(express.json());

// Import product routes
const productRoutes = require("./routes/product");
app.use("/products", productRoutes);

// Use the port provided by the environment (Render sets PORT)
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
