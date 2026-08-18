export interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'PROVIDER' | 'CUSTOMER';
  firstName: string;
  lastName: string;
  phone?: string;
  avatarUrl?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface ProviderProfile {
  id: string;
  userId: string;
  name: string;
  description?: string;
  category: 'HEALTHCARE' | 'COWORKING' | 'CONSULTING' | 'EVENTS' | 'OTHER';
  location?: string;
  timezone: string;
  vacationMode: boolean;
}

export interface Slot {
  id: string;
  providerProfileId: string;
  startTime: string;
  endTime: string;
  durationMinutes: number;
  status: 'AVAILABLE' | 'BOOKED' | 'BLOCKED' | 'CANCELLED';
  title?: string;
  notes?: string;
}

export interface Booking {
  id: string;
  slot: Slot;
  customer: User;
  status: 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW';
  notes?: string;
  bookedAt: string;
  cancelledAt?: string;
  cancellationReason?: string;
}

export interface Waitlist {
  id: string;
  slotId: string;
  customerId: string;
  position: number;
  status: 'WAITING' | 'PROMOTED' | 'EXPIRED' | 'CANCELLED';
  joinedAt: string;
}

export interface Notification {
  id: string;
  type: string;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export interface ProviderDashboardStats {
  totalSlots: number;
  bookedSlots: number;
  availableSlots: number;
  occupancyRate: number;
  totalBookings: number;
}

export interface AdminDashboardStats {
  totalUsers: number;
  totalProviders: number;
  totalBookings: number;
  totalActiveBookings: number;
  totalCancelledBookings: number;
  totalSlots: number;
  totalAvailableSlots: number;
}
