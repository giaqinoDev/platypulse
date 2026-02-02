import { useState, useEffect } from "react";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState("");

    //Data Fetching
    const BASE_URL = "https://registry.npmjs.org";
    const [queriedPackage, setQueriedPackage] = useState(null);

    useEffect(() => {
        const fetchPackage = async () => {
            //Using CORS Proxy Server
            const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_URL}/${query}`));
            const packageInfo = await response.json();
            setQueriedPackage(packageInfo);
        };
        fetchPackage();
    }, [query]);

    useEffect(() => {
        console.log(queriedPackage);
    }, [queriedPackage])

    function handleSubmit(event){
        event.preventDefault();
        setQuery(searchTerm.trim())
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

            <div>
                {queriedPackage && queriedPackage._rev}
            </div>
        </div>
    );
}

export default Home;