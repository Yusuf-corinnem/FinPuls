export interface IAuthStore {
    isAuthenticated: boolean;
    login: string;
    password: string;
    setCredentials(login: string, password: string): void;
    signIn(): void;
    signOut(): void;
}