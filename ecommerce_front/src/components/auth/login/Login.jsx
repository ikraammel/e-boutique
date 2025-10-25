import React, { useState } from 'react'
import { authenticate } from '../../Api'
import { toast, ToastContainer } from 'react-toastify'
import { useNavigate } from 'react-router-dom'
import './Login.css'
import { FaEye, FaEyeSlash } from 'react-icons/fa'
import { useAuth } from '../../contexts/AuthContext'
import LoginGoogle from './LoginGoogle'

function Login() {
    const {login} = useAuth()
    const[email,setEmail] = useState("")
    const[password,setPassword] = useState("")
    const[loading,setLoading] = useState(false)
    const[error,setError] = useState("")
    const[showPassword,setShowPassword] = useState(false)

    const navigate = useNavigate()

    const handleChange = e => {
        const {name,value} = e.target
        if(name === "email") setEmail(value)
        if(name === "password") setPassword(value)
    }
    const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    try {
        const response = await authenticate(email, password)

        if (response.data?.token) {
            login(response.data.token)
            localStorage.setItem("token", response.data.token)
            toast.success("Connexion réussie !")
            setTimeout(() => {
                navigate("/")
            }, 1000)
        }
    } catch (err) {
        console.error("Erreur login:", err)
        setError("Email ou mot de passe incorrect.")
        toast.error("Email ou mot de passe incorrect ❌")
    } finally {
        setLoading(false)
    }
}

  return (
    <form className='login-form' onSubmit={handleSubmit}>
        <label>Email</label>
        <input type="email" name="email" value={email} onChange={handleChange} required/>

        <label>Password</label>
        <div className='password-container'>
        <input type={showPassword ? "text" : "password"} name="password" 
                value={password} onChange={handleChange} required/>
                <span className='password-toggle-icon' onClick={() => setShowPassword(!showPassword)}>
                    {showPassword ? <FaEyeSlash/> : <FaEye/>}
                </span>
        </div>
        <button type='submit' disabled={loading}>
            {loading ? "Connexion" : "Se connecter"}
        </button>
        <LoginGoogle/>
        <p className='link-text'>
            N'avez-vous pas de compte ? <span className="link" onClick={() => navigate("/registrate")}>S'inscrire</span>
        </p>

        {error && <p style={{color: 'red'}}>{error}</p>}
        <ToastContainer/>
    </form>
  )
}

export default Login
