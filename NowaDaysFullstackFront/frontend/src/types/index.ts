export interface Product {
  id: string;
  name: string;
  price: number;
  stockQuantity: number;
}

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  balance: number;
}

export interface OrderHistoryItem {
  id: string;
  userId: string;
  productId: string;
  totalPrice: number;
  status: string;
  createdAt: string;
}