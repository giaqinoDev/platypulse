import { useState } from "react";

function Home(){
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState();
    function handleSubmit(event){
        setQuery(event.target.value.trim())
    }
    return(
        <div>
            <h1>Package Radar</h1>
            <form onSubmit={handleSubmit}>
                <input 
                type="search" 
                name="searchTerm" 
                required
                value={searchTerm}
                onChange={(event) => {setSearchTerm(event.target.value)}}/>
                <input type="submit"/>
            </form>
        </div>
    );
}

export default Home;