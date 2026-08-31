export interface Product {
  id: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  category?: string;
}

export interface ProductSearchQuery {
  name?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  page: number;
  size: number;
}

export interface PagedResult<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  balance: number;
}


export type OrderStatus = 'PENDING' | 'COMPLETED' | 'CANCELLED';

export interface OrderHistoryItem {
  id: string;
  userId: string;
  productId: string;
  quantity: number;
  totalPrice: number;
  status: OrderStatus;
  createdAt?: string;
}