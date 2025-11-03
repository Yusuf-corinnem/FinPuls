import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../store/auth';

export default function Login() {
    const { setCredentials, signIn } = useAuth();
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');

    function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setCredentials(login, password);
        signIn();
        navigate('/app');
    }

    return (
        <form onSubmit={onSubmit} style = {{maxWidth: 480, display: 'flex', flexDirection: 'column', gap: 16}}>
            <h2>Entering</h2>
            <input placeholder="Login" value={login} onChange={(e) => setLogin(e.target.value)} required/>
            <input placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required/>
            <button type="submit">Enter</button>
        </form> 
    );
}