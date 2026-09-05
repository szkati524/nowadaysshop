import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ShopPage } from './pages/ShopPage';
import { ProfilePage } from './pages/ProfilePage';
import { ContactPage } from './pages/ContactPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { OrderHistoryPage } from './pages/OrderHistoryPage';
import { ProductDetailsPage } from './pages/ProductDetailsPage';
import { CartProvider } from './context/CartContext';
import { AddProduct } from './pages/AddProductPage';


function App() {
  return (
    <AuthProvider>
      <CartProvider>
      <Router>
        <div className="min-h-screen bg-slate-50 text-slate-900">
          <Navbar />
          <Routes>
            <Route path="/" element={<ShopPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/contact" element={<ContactPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="order-history" element={<OrderHistoryPage />} />
            <Route path="/products/:id" element = {<ProductDetailsPage />} />
            <Route path ="/add-product" element={<AddProduct />} />
          </Routes>
        </div>
      </Router>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;