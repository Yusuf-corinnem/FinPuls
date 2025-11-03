import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth} from './store/auth';
import Login from './pages/Login';

function RequireAuth() {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace/>;
}

function Dashboard() {
  return <div style={{padding: '20px', justifyContent: 'center', alignItems: 'center'}}>HELLLOOOOOO MULTIBAAAANK </div>;
}

function App() {
  return (  
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<RequireAuth />}>
          <Route path="/app" element={<Dashboard />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace/>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;


