import { useParams } from "react-router";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./AboutPage.css"
import useNPMFetch from "../NPMFetch/useNPMFetch";
import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";

function AboutPage(){
    const {ecosystem, packageName} = useParams();
    const [query, setQuery] = useState<SearchContextStructure|null>(null);
    const exResult = useNPMFetch(query);

    useEffect(() => {
        if(!ecosystem || !packageName){
            return;
        }

        const constructedQuery : SearchContextStructure = {
            search_term: packageName.split("/"),
            ecosystem: ecosystem
        }

        setQuery(constructedQuery);
        
    }, [ecosystem, packageName])
    return(
        <div>
            <MenuHeader/>
            <div className="about-page-body">
                <h1 className="about-page-hero">Package Health Insights for Modern Software Development</h1>
                <p className="about-page-subheading">Platypulse analyzes open-source packages across ecosystems and transforms hundreds of maintenance, 
                    adoption, activity, and risk signals into a single health score.</p>
            </div>
            <div>
                
            </div>
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

export default AboutPage;