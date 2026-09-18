import { Route, Routes } from 'react-router'
import './App.css'
import Home from './HomePage/Home'
import ReportPage from './ReportPage/ReportPage'

function App() {
  return (
    <Routes>
      <Route path="/home" element={<Home/>}/>
      <Route path="/package/:ecosystem/:packageName/latest" element={<ReportPage/>}/>
      <Route path="/package/:ecosystem/:scope/:packageName/latest" element={<ReportPage/>}/>
      <Route path="/package/:ecosystem/:scope/:packageName/id/:report_id" element={<ReportPage/>}/>
      <Route path="/package/:ecosystem/:packageName/id/:report_id" element={<ReportPage/>}/>

      <Route path="*" element={<Home/>}/>
    </Routes>
  )
}

export default App
