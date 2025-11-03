import { create } from 'zustand';
import type { IAuthStore } from '../types/index';

export const useAuth = create<IAuthStore>((set) => ({
    isAuthenticated: false,
    login: '',
    password: '',
    setCredentials(login: string, password: string) {
        set({ login, password });
    },
    signIn() {
        set({ isAuthenticated: true });
    },
    signOut() {
        set({ isAuthenticated: false, login: '', password: '' });
    }
}));
