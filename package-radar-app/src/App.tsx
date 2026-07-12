import { Route, Routes } from 'react-router'
import './App.css'
import Home from './HomePage/Home'
import AboutPage from './AboutPage/AboutPage'

function App() {
  return (
    <Routes>
      <Route path="/home" element={<Home/>}/>
      <Route path="/package/:ecosystem/:packageName" element={<AboutPage/>}/>
      <Route path="*" element={<Home/>}/>
    </Routes>
  )
}

export default App
