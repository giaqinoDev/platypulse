import { Route, Routes } from 'react-router'
import './App.css'
import Home from './HomePage/Home'
import { useState } from 'react'
import RegistrySearchContext from './contexts/RegistrySearch/RegistrySearchContext'
import type { SearchContextStructure } from './contexts/RegistrySearch/SearchContextStructure'
import AboutPage from './AboutPage/AboutPage'

function App() {
  const [registrySearchTerm, setRegistrySearchTerm] = useState<SearchContextStructure| null>(null);
  return (
    <RegistrySearchContext.Provider value={{registrySearchTerm, setRegistrySearchTerm}}>
      <Routes>
        <Route path="/home" element={<Home/>}/>
        <Route path="/package/:ecosystem/:packageName" element={<AboutPage/>}/>
        <Route path="*" element={<Home/>}/>
      </Routes>
    </RegistrySearchContext.Provider>
  )
}

export default App
