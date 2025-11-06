import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../store/auth';

export default function Login() {
    const { setCredentials, signIn } = useAuth();
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);

        const trimmedLogin = login.trim();
        const trimmedPassword = password.trim();

        // Простая синхронная валидация до API
        if (trimmedLogin.length < 3) {
            setError('Логин должен содержать минимум 3 символа');
            return;
        }
        if (trimmedPassword.length < 3) {
            setError('Пароль должен содержать минимум 3 символа');
            return;
        }

        setIsSubmitting(true);

        // Имитируем сетевой запрос к API
        setTimeout(() => {
            // Условие успеха как плейсхолдер: demo/demo
            const isOk = trimmedLogin === 'demo' && trimmedPassword === 'demo';
            if (!isOk) {
                setError('Неверные логин или пароль');
                setIsSubmitting(false);
                return;
            }

            setCredentials(trimmedLogin, trimmedPassword);
            signIn();
            navigate('/app');
            setIsSubmitting(false);
        }, 700);
    }

    return (
        <form onSubmit={onSubmit} style={{ maxWidth: 480, display: 'flex', flexDirection: 'column', gap: 16 }}>
            <h2>Вход</h2>
            <input placeholder="Логин" value={login} onChange={(e) => setLogin(e.target.value)} required />
            <input type="password" placeholder="Пароль" value={password} onChange={(e) => setPassword(e.target.value)} required />
            {error && (
                <div style={{ color: 'red' }} aria-live="polite">{error}</div>
            )}
            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Входим…' : 'Войти'}
            </button>
        </form>
    );
}