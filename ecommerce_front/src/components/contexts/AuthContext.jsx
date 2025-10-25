import { createContext, useContext, useState, useEffect } from "react";
import {jwtDecode} from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null); 
  const [token, setToken] = useState(null);

  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    if (savedToken) {
      const decoded = jwtDecode(savedToken);
      setToken(savedToken);
      setUser(decoded);
    }
  }, []);

  const login = (token) => {
    localStorage.setItem("token", token);
    const decoded = jwtDecode(token);
    setToken(token);
    setUser(decoded);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook for easy access
export const useAuth = () => useContext(AuthContext);