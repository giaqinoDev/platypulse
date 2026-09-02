import { Route, Routes } from 'react-router'
import './App.css'
import Home from './HomePage/Home'
import AboutPage from './ReportPage/ReportPage'

function App() {
  return (
    <Routes>
      <Route path="/home" element={<Home/>}/>
      <Route path="/package/:ecosystem/:packageName/latest" element={<AboutPage/>}/>
      <Route path="/package/:ecosystem/:scope/:packageName/latest" element={<AboutPage/>}/>

      <Route path="*" element={<Home/>}/>
    </Routes>
  )
}

export default App
