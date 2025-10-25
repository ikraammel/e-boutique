import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { FaEye, FaEyeSlash } from 'react-icons/fa'
import { toast, ToastContainer } from 'react-toastify'
import { registrate } from '../../Api'
import './Registrate.css';


function Registrate() {
    const[firstName,setFirstName] = useState("")
    const[lastName,setLastName] = useState("")
    const[email,setEmail] = useState("")
    const[password,setPassword] = useState("")
    const[confirmPassword,setConfirmPassword] = useState("")
    const[loading,setLoading] = useState(false)
    const[error,setError] = useState("")
    const[showPassword,setShowPassword] = useState(false)
    const[showConfirmPassword,setShowConfirmPassword] = useState(false)

    const navigate = useNavigate()

    const handleChange = e => {
        const {name,value} = e.target
        if(name === "firstName") setFirstName(value)
        if(name === "lastName") setLastName(value)    
        if(name === "email") setEmail(value)
        if(name === "password") setPassword(value)
        if(name === "confirmPassword") setConfirmPassword(value)
    }
    const handleSubmit = async(e) => {
        e.preventDefault()
        setLoading(true)
        setError("")
        if(password != confirmPassword){
            setError("Les mots de passe ne correspondent pas !");
            toast.error("Les mots de passe ne correspondent pas ❌");
            setLoading(false);
            return;
        }
        try{
            const response = await registrate(firstName,lastName,email,password)
            if (response.data?.token) {
            localStorage.setItem("token", response.data.token);
        }
            toast.success("Inscription réussie !")
            setTimeout(() => {
                navigate("/")
            }, 1000)
        } catch (err) {
            console.error(err)
            if (err.response) {
            const status = err.response.status
            const message = err.response.data?.message || ""
            if (status === 400 || status === 409) {
                setError("Cet email est déjà utilisé !")
                toast.error("Cet email est déjà utilisé !")
            } else {
                setError("Erreur lors de l'inscription !")
                toast.error("Erreur lors de l'inscription !")
            }
        } else {
            setError("Erreur lors de l'inscription !")
            toast.error("Erreur lors de l'inscription !")
        }
        } finally {
            setLoading(false)
        }
    }

  return (
    <form className='register-form' onSubmit={handleSubmit}>
        <label>Prénom</label>
        <input type="text" name="firstName" value={firstName} onChange={handleChange} required/>

        <label>Nom</label>
        <input type="text" name="lastName" value={lastName} onChange={handleChange} required/>

        <label>Email</label>
        <input type="email" name="email" value={email} onChange={handleChange} required/>

        <label>Mot de passe</label>
        <div className='password-container'>
        <input type={showPassword ? "text" : "password"} name="password" 
                value={password} onChange={handleChange} required/>
                <span className='password-toggle-icon' onClick={() => setShowPassword(!showPassword)}>
                    {showPassword ? <FaEyeSlash/> : <FaEye/>}
                </span>
        </div>

        <label>Confirmer le mot de passe</label>
        <div className='password-container'>
        <input type={showConfirmPassword ? "text" : "password"} name="confirmPassword" 
                value={confirmPassword} onChange={handleChange} required/>
                <span className='password-toggle-icon' onClick={() => setShowPassword(!showPassword)}>
                    {showConfirmPassword ? <FaEyeSlash/> : <FaEye/>}
                </span>
        </div>

        <button type='submit' disabled={loading}>
            {loading ? "Inscription" : "S'inscrire"}
        </button>
        <p>
            Avez-vous déjà un compte ? <span className="link" onClick={() => navigate("/login")}>Se connecter</span>
        </p>


        {error && <p style={{color: 'red'}}>{error}</p>}
        <ToastContainer/>
    </form>
  )
}

export default Registrate
