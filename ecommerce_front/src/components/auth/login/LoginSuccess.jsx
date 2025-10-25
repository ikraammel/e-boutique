import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function LoginSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get("token");
    const tokenStored = localStorage.getItem("token");

    console.log("Token URL :", tokenFromUrl);
    console.log("Token localStorage :", tokenStored);

    if (tokenFromUrl) {
      localStorage.setItem("token", tokenFromUrl);
      navigate("/"); 
    } else if (tokenStored) {
      navigate("/"); // token déjà présent → redirige vers "/"
    } else {
      navigate("/login");
    }
  }, [navigate]);

  return <p>Connexion en cours...</p>;
}

export default LoginSuccess;
