import { useParams } from "react-router";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./AboutPage.css"
import useNPMFetch from "../NPMFetch/useNPMFetch";
import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import SideNavBar from "./SideNavBarComponents/SideNavBar";
import HealthScoreRadialChart from "../util-components/ReportVisuals/HealthScoreRadialChart";

function AboutPage(){
    const {ecosystem, scope, packageName} = useParams();
    const [query, setQuery] = useState<SearchContextStructure|null>(null);
    const exResult = useNPMFetch(query);

    useEffect(() => {
        if(!ecosystem || !packageName){
            return;
        }

        let constructedQuery : SearchContextStructure;
        if(scope){
            constructedQuery = {
                search_term: ["@" + scope, packageName],
                ecosystem: ecosystem
            }
        }
        else{
            constructedQuery = {
                search_term: [packageName],
                ecosystem: ecosystem
            }
        }

        setQuery(constructedQuery);

    }, [ecosystem, scope, packageName])
    return(
        <div className="report-page-root">
            <SideNavBar/>
            <div className="report-page-body">
                <MenuHeader/>
                <div>
                    <h1>{exResult.packageData?.raw_signals.package_info.pkge_name}</h1>
                    <h3>{exResult.packageData?.raw_signals.package_info.description}</h3>
                    <HealthScoreRadialChart input={exResult.packageData?.["overall"]}/>
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
        </div>
    );
}

export default AboutPage;