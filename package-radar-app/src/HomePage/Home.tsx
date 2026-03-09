import { useState } from "react";
import useNPMFetch from "../NPMFetch/useNPMFetch";

function Home(){

    //Search Term
    const [searchTerm, setSearchTerm] = useState("");
    const [query, setQuery] = useState<string|null>(null);
    const exResult = useNPMFetch(query);

    function handleSubmit(event: React.FormEvent<HTMLFormElement>){
        event.preventDefault();
        setQuery(encodeURIComponent(searchTerm.trim()));
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
            {exResult.libIo_dependents &&
                <>
                    <div>Dependents (Counts published packages and measures library to library use): {exResult.libIo_dependents.num_dependents}</div>
                    <div>Dependent Repos (Counts code repositories and measures actual project adoption): {exResult.libIo_dependents.num_dependent_repos}</div>
                </>
            }

            
        </div>
    );
}

export default Home;