import { useState } from "react";
import useNPMFetch from "../NPMFetch/useNPMFetch";
import useTestFetch from "../NPMFetch/useTestFetch";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState<string[]|null>(null);
    const exResult = useNPMFetch(query);
    const {a, b, c, triggerFetch} = useTestFetch();
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
            <h1>Packround Check</h1>
            <form onSubmit={handleSubmit}>
                <input 
                type="search" 
                name="searchTerm" 
                required
                value={searchTerm}
                onChange={(event) => {setSearchTerm(event.target.value)}}/>
                <input type="submit"/>
            </form>
            <button onClick={() => {
                triggerFetch();
                console.log("Pressed");
            }
            }>
                Test GitHub Registry Response
            </button>

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