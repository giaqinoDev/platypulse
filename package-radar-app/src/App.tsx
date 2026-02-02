import { Route, Routes } from 'react-router'
import './App.css'
import Home from './HomePage/Home'

function App() {

  return (
    <Routes>
      <Route path="/home" element={<Home/>}/>
      <Route path="*" element={<Home/>}/>
    </Routes>
  )
}

export default App
