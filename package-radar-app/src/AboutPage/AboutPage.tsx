import { useParams } from "react-router";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./AboutPage.css"
import useNPMFetch from "../NPMFetch/useNPMFetch";
import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import SideNavBar from "./SideNavBarComponents/SideNavBar";
import HealthScoreRadialChart from "../util-components/ReportVisuals/HealthScoreRadialChart";
import { FaNpm } from "react-icons/fa";
import { FaSquareGithub } from "react-icons/fa6";
import SubScoreChart from "../util-components/report-components/sub-score/SubScoreChart";




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
                <div className="report-overview-header">
                    <div className="overview-header-container">
                        <h1 id="overview-header-title">{exResult.packageData?.raw_signals?.package_info?.pkge_name}</h1>
                        <p id="overview-sub-header">{exResult.packageData?.raw_signals?.package_info?.description}</p>
                        <div className="icons-header">
                            <a href={"https://www.npmjs.com/package/" + exResult.packageData?.raw_signals?.package_info?.pkge_name} target="_blank">
                                <FaNpm className="reg-icon" id="npm-icon"/>
                            </a>
                            {exResult.packageData?.raw_signals?.package_info?.latest_version_info?.repository?.url &&
                                <a href={"https://github.com" + exResult.packageData?.raw_signals?.package_info?.latest_version_info?.repository?.url.split("github.com")[1]} target="_blank">
                                    <FaSquareGithub className="reg-icon" id="github-icon"/>
                                </a>
                            }
                        </div>
                    </div>
                    <div className="health-score-container">
                        <HealthScoreRadialChart input={exResult.packageData?.["overall"]}/>
                    </div>
                </div>
                <SubScoreChart category={"Activity"} score={exResult.packageData?.["activity"]}/>
                <SubScoreChart category={"Maintenence"} score={exResult.packageData?.["maintenance"]}/>
                <SubScoreChart category={"Adoption"} score={exResult.packageData?.["adoption"]}/>
                <SubScoreChart category={"Risk"} score={exResult.packageData?.["risk"]}/>
                
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