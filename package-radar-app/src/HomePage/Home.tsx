import { useState, useEffect } from "react";
import useNPMFetch from "../NPMFetch/useNPMFetch";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState("");
    const exResult = useNPMFetch({queriedSearch: query});

    //Data Fetching
    //const BASE_URL = "https://registry.npmjs.org";
    const [queriedPackage, setQueriedPackage] = useState(null);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Dev Lesson: Defining async functiosn inside useEffect() for data fetchign is common practice as defining it outside of useEffect would require the function
        // to be passed as a parameter in the useEffect hook which would then cause an infinite loop as the function is a new instance on each render

        //NPM package fetching
        /*
        const fetchNPMPackage = async () => {
            setLoading(true);
            setQueriedPackage(null);
            setError(null);
            try{
                //Using CORS Proxy Server
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_URL}/${query}`));
                if(response.status >= 500){
                    throw new Error("Server error")
                }
                else if(response.status >= 400){
                    throw new Error("Client error")
                }
                const packageInfo = await response.json();
                const latest_Release = packageInfo['dist-tags'].latest;
                const release_date = packageInfo.time[latest_Release];
                console.log(latest_Release);
                setQueriedPackage(release_date);
            }
            catch(error: any){
                console.log("erorr caught")
                setError(error.message);
            }
            finally{
                setLoading(false);
            }
        };

        fetchNPMPackage();
        */
    }, [query]);

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

            {loading && <div>Loading</div>}
            {error && <div id="error">{error}</div>}
            {/*queriedPackage && <div id="info">
                {queriedPackage}
                <p></p>
            </div>*/}
            {
                exResult && <div id="info">
                    {exResult.queryResult}
                    <p></p>
                </div>
            }
        </div>
    );
}

export default Home;