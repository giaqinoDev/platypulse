import { useState } from "react";
import "@fontsource/geist-sans/400.css";
import "@fontsource/geist-sans/200.css";
import { IoSearchOutline } from "react-icons/io5";
import "./Home.css";
import useNPMFetch from "../NPMFetch/useNPMFetch";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState<string[]|null>(null);
    const exResult = useNPMFetch(query);
    function handleSubmit(event: React.FormEvent<HTMLFormElement>){
        event.preventDefault();
        setQuery(parseSearchTerm(searchTerm.trim()));
        
    }

    function parseSearchTerm(searchTerm : string){
        const parsedSearchTerm = searchTerm.split("/")
        return parsedSearchTerm;
    }

    return(
        <div>
            <h1 className="title">Packground Check</h1>
            <p className="header">Packground Check continuously monitors health signals across the npm ecosystem, 
                helping developers make more informed decisions about which packages to include in their projects.</p>
            <form onSubmit={handleSubmit}>
                <div className="search-field-container">
                    <IoSearchOutline className="search-icon"/>
                    <input
                        className="package-search-bar" 
                        type="search" 
                        placeholder="Analyze Package.."
                        name="searchTerm" 
                        required
                        value={searchTerm}
                        onChange={(event) => {setSearchTerm(event.target.value)}}/>
                    <input type="submit"/>
                </div>
            </form>

            {exResult.loading && 
                <div>Loading</div>
            }

            {exResult.fetchError && 
                <div id="info">
                    <p>{exResult.fetchError.toString()}</p>
                </div>
            }
            {exResult.packageData && 
                <div id="info">
                    {Object.entries(exResult.packageData).map(([key, value]) => (
                        <div key={key}>
                            <p>{`${key}: ${String(value)}`}</p>
                        </div>
                    ))}
                </div>
            }
            
        </div>
    );
}

export default Home;