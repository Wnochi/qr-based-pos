function loginUser(email, password) {
  const cleanEmail = email.toLowerCase().trim();

  const mockUsers = [
    {
      email: "adminm",
      password: "admin123",
      role: "admin" // Routes to admin.html
    },
    {
      email: "cashier@gmail.com",
      password: "cashier123",
      role: "cashier" // Routes to cashier.html
    }
  ];

  const foundUser = mockUsers.find(user => user.email === cleanEmail && user.password === password);

  if (foundUser) {
    return {
      success: true,
      message: "Login successful!",
      role: foundUser.role // Pass the role back to the HTML page
    };
  } else {
    return {
      success: false,
      message: "Invalid email or password."
    };
  }
}