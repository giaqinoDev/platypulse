import { useLocation, useParams } from "react-router";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./AboutPage.css"
import useNPMFetch from "../NPMFetch/useNPMFetch";
import { useEffect, useMemo } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import SideNavBar from "./SideNavBarComponents/SideNavBar";
import HealthScoreRadialChart from "../util-components/ReportVisuals/HealthScoreRadialChart";
import { FaNpm } from "react-icons/fa";
import { FaSquareGithub } from "react-icons/fa6";
import SubScoreChart from "../util-components/report-components/sub-score/SubScoreChart";
import MaintenanceQuickLook from "../util-components/report-components/quick-looks/MaintenanceQuickLook";
import CommunityQuickLook from "../util-components/report-components/quick-looks/CommunityQuickLook";
import { BarLoader } from "react-spinners";
import DownloadsChart from "../util-components/ReportVisuals/DownloadsChart";
import ContributorsQuickLook from "../util-components/report-components/quick-looks/ContributorsQuickLook";
import RepositoryQuickLook from "../util-components/report-components/quick-looks/RepositoryQuickLook";
import CommitsChart from "../util-components/ReportVisuals/CommitsChart";
import DependencyGraph from "../util-components/report-components/dependency-graph/DependencyGraph";




function AboutPage(){
    const {ecosystem, scope, packageName} = useParams();
    const location = useLocation();
    // const [query, setQuery] = useState<SearchContextStructure|null>(null);
    const query = useMemo<SearchContextStructure | null>(() => { //useMemo lets you store the result of a calculation between renders
        if (!ecosystem || !packageName) {
            return null;
        }

        return {
            search_term: scope ? ["@" + scope, packageName] : [packageName],
            ecosystem
        };
    }, [ecosystem, scope, packageName]);
    const exResult = useNPMFetch(query);

    useEffect(() => {
        if(location.hash){
            console.log(location.hash);
            const element = document.getElementById(location.hash.split("#")[1]);
            if(element){
                element.scrollIntoView();
            }
        }
    }, [location.hash])
    return(
        <div className="report-page-root">
            <SideNavBar/>
            <div className="report-page-body">
                <MenuHeader/>
                    <div className="report-body" style={{"justifyContent": exResult.loading ? "center": "initial"}}>
                        {exResult.loading == false && 
                            <div className="report-content">
                                <section id="overview-section">
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
                                    <div className="quick-look-container">
                                        <div id="sub-scores">
                                            <SubScoreChart category={"Activity"} score={exResult.packageData?.["activity"]}/>
                                            <SubScoreChart category={"Maintenence"} score={exResult.packageData?.["maintenance"]}/>
                                            <SubScoreChart category={"Adoption"} score={exResult.packageData?.["adoption"]}/>
                                            <SubScoreChart category={"Risk"} score={exResult.packageData?.["risk"]}/>
                                        </div>
                                        <MaintenanceQuickLook data={exResult.packageData}/>
                                        <CommunityQuickLook data={exResult.packageData}/>
                                    </div>
                                </section>
                                <section id="community-section">
                                    <DownloadsChart data={exResult.packageData?.raw_signals?.downloads_timeline}/>
                                </section>
                                <section id="maintenance-section">
                                    <CommitsChart data={exResult.packageData?.raw_signals?.git_hub_reg?.commit_info?.commit_timeline_volume}/>
                                    <div className="quick-look-container">
                                        <ContributorsQuickLook data={exResult.packageData}/>
                                        <RepositoryQuickLook data={exResult.packageData}/>
                                    </div>
                                </section>
                                <section id="dependencies-section">
                                    <DependencyGraph dependencies={exResult.packageData?.raw_signals?.package_info.latest_version_info?.dependency_tree}/>
                                </section>
                            </div>
                        }
                        {exResult.loading == true &&
                            <div id="loading-visual">
                                <p style={{"color": "#293241"}}>Grabbing Package...</p>
                                <BarLoader color="#ff6139"/>
                            </div>
                        }
                    </div>
            </div>
        </div>
    );
}

export default AboutPage;