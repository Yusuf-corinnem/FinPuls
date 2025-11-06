import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from './store/auth';
import Login from './pages/Login';

function RequireAuth() {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

function Dashboard() {
  const navigate = useNavigate();
  const { signOut } = useAuth();
  return (
    <div style={{ padding: 20, display: 'flex', gap: 12, alignItems: 'center' }}>
      <div>HELLLOOOOOO MULTIBAAAANK</div>
      <button
        onClick={() => {
          signOut();
          navigate('/login');
        }}
      >
        Выйти
      </button>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<RequireAuth />}>
          <Route path="/app" element={<Dashboard />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;


