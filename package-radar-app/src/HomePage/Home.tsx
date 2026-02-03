import { useState, useEffect } from "react";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState("");

    //Data Fetching
    const BASE_URL = "https://registry.npmjs.org";
    const [queriedPackage, setQueriedPackage] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState();

    useEffect(() => {
        const fetchPackage = async () => {
            setLoading(true);
            try{
                //Using CORS Proxy Server
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_URL}/${query}`));
                if(!response.ok){
                    throw new Error("Oh shiiiit")
                }
                const packageInfo = await response.json();
                setQueriedPackage(packageInfo);
            }
            catch(error){
                setError(error);
            }
            setLoading(false);
        };
        fetchPackage();
    }, [query]);

    useEffect(() => {
        console.log(queriedPackage);
    }, [queriedPackage])

    function handleSubmit(event: any){
        setQueriedPackage(null);
        setError(null);

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

            {loading && <div>Loading</div>}
            {error && <div id="error">AAHAHHA</div>}
            {queriedPackage && <div id="info">
                {queriedPackage._rev}
            </div>}
        </div>
    );
}

export default Home;