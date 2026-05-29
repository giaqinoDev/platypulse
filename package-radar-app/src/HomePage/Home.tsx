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
            <h1 className="title">Make dependency decisions backed by signals, not guesswork.</h1>
            <p className="header">
                Monitor the health of packages across the NPM ecosystem and retrieve up to date reporting to guide decisions on package inclusion.
            </p>
            <form onSubmit={handleSubmit}>
                <div className="search-field-container">
                    <IoSearchOutline className="search-icon"/>
                    <input
                        className="package-search-bar" 
                        type="text" 
                        placeholder="Analyze Package..."
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
                    <p className="info">{exResult.fetchError.toString()}</p>
                </div>
            }
            {exResult.packageData && 
                <div id="info">
                    {Object.entries(exResult.packageData).map(([key, value]) => (
                        <div key={key}>
                            <p className="info">{`${key}: ${String(value)}`}</p>
                        </div>
                    ))}
                </div>
            }
            
        </div>
    );
}

export default Home;