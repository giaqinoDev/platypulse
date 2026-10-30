import { useLocation, useNavigate, useParams } from "react-router";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";
import "./ReportPage.css"
import useNPMFetch from "../NPMFetchHook/useNPMFetch";
import { useEffect, useMemo, useRef, useState } from "react";
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
import ReportDashboardNav from "./ReportDashboardNav/ReportDashboardNav";
import { BsBarChartFill } from "react-icons/bs";
import useFetchSnapShots from "../FetchSnapShotsHook/useFetchSnapShots";
import useGenerateSnapShot from "../generateSnapShotHook/useGenerateSnapShotHook";
import SnapShots from "./SnapShotsSection/SnapShots";





function ReportPage(){
    const {ecosystem, scope, packageName, reportId} = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const [viewPortWidth, setViewPortWidth] = useState(window.innerWidth);
    const [reportActiveNav, setReportActiveNav] = useState<number>(1);

    const abortControllerRef = useRef<AbortController | null>(null); /*Persists across renders*/
    const [abortSignal, setAbortSignal] = useState<AbortSignal|null>(null);

    const {
        getSnapShots, 
        snapShotsLoading, 
        fetchError, 
        fetchedSnapShots
    } = useFetchSnapShots(abortSignal);

    const {
        generateNewReport, 
        generatingReport, 
        generationError, 
        generatedReportId
    } = useGenerateSnapShot(abortSignal);

    const snapshotsRetrievedFor = useRef<string | null>(null);


    useEffect(() => {
        //Cancel the previous request session
        abortControllerRef.current?.abort();

        //Create a new controller for the new request session
        const abortController = new AbortController();

        abortControllerRef.current = abortController;
        setAbortSignal(abortController.signal);
        //Component dismount
        return () => {
            abortController.abort();
        }
    }, [location.pathname]);

    const query = useMemo<SearchContextStructure | null>(() => { //useMemo lets you store the result of a calculation between renders
        if (!ecosystem || !packageName) {
            return null;
        }
        setAbortSignal(null);
        return {
            search_term: scope ? ["@" + scope, packageName] : [packageName],
            ecosystem: ecosystem,
            reportId: reportId
        };
    }, [ecosystem, scope, packageName, reportId]);
    const reportFetchResponse = useNPMFetch(query, abortSignal);

    async function handleSnapShotRetreival(){
        const packageId = reportFetchResponse.api_response?.data?.package_id;

        if(!packageId){
            return;
        }

        //Only allow one request for this route
        if(snapshotsRetrievedFor.current === location.pathname){
            return;
        }

        if(snapShotsLoading){
            return;
        }
        await getSnapShots(reportFetchResponse.api_response?.data?.package_id);
        console.log("get snap shot request made")
        
        snapshotsRetrievedFor.current = location.pathname;
    }

    async function handleNewReportGen(){
        const package_id = reportFetchResponse?.api_response?.data?.package_id;
        if(package_id){
            await generateNewReport(package_id);
        }
    }

    useEffect(() => {
        if(!generatedReportId){
            return;
        }
        if(query?.search_term.length == 2){
            console.log(generatedReportId);
            navigate(`/package/${query.ecosystem}/${query.search_term[0].replace("@", "")}/${query.search_term[1]}/id/${generatedReportId}`);
        }
        else if(query?.search_term.length == 1){
            console.log(generatedReportId);
            navigate(`/package/${query.ecosystem}/${query.search_term[0]}/id/${generatedReportId}`);
        }
    }, [generatedReportId])

    function viewPortSetter(){
        setViewPortWidth(window.innerWidth)
    }

    useEffect(() => {
        //load: waits to execute when windows resources are finished loading
        window.addEventListener("load", viewPortSetter)
        //resize: waits to execute when window is resized
        window.addEventListener("resize", viewPortSetter)

        return () => {
            window.removeEventListener("load", viewPortSetter);
            window.removeEventListener("resize", viewPortSetter);
        }
    }, [])
    

    useEffect(() => {
        if(location.hash){
            console.log(location.hash);
            const element = document.getElementById(location.hash.split("#")[1]);
            if(element){
                element.scrollIntoView();
            }
        }
    }, [location.hash])

    useEffect(() => {
        console.log(reportFetchResponse.api_response?.success);
        console.log(reportFetchResponse.fetchError);
    }, [reportFetchResponse.api_response, reportFetchResponse.fetchError])

    return(
        <div className="report-page-root">
            {viewPortWidth > 600 && <SideNavBar/>}
            <div className="report-page-body">
                <MenuHeader/>
                    <div className="report-body" style={{"justifyContent": (reportFetchResponse.loading || snapShotsLoading || generatingReport) ? "center": "initial"}}>
                        {(reportFetchResponse.loading == false && generatingReport == false && reportFetchResponse?.api_response?.success && reportFetchResponse.fetchError == null) && 
                            <div className="report-content">
                                <ReportDashboardNav handleSnapShotRetreival={handleSnapShotRetreival} setReportActiveNav={setReportActiveNav}/>
                                {reportActiveNav == 1 && 
                                <div>
                                    <section id="overview-section">
                                        {reportFetchResponse.api_response?.data?.can_analyze &&
                                            <div id="generate-new-report-container">
                                                <BsBarChartFill style={{"marginRight": "5px"}}/>
                                                <p>A new report is ready to generate!</p>                                                
                                                <button id="generate-report-button" onClick={handleNewReportGen}>
                                                    Generate Report
                                                </button>
                                            </div>
                                        }

                                        <div id="generation-info">
                                            <p id="generation-info-text">Report generated on {reportFetchResponse.api_response?.data?.creation_date_time.toDateString()}, {reportFetchResponse.api_response?.data?.creation_date_time.toLocaleTimeString()}</p>
                                        </div>
                                        <div className="report-overview-header">
                                            <div className="overview-header-container">
                                                <h1 id="overview-header-title">{reportFetchResponse.api_response?.data?.raw_signals?.package_info?.pkge_name}</h1>
                                                <p id="overview-sub-header">{reportFetchResponse.api_response?.data?.raw_signals?.package_info?.description}</p>
                                                <div className="icons-header">
                                                    <a href={"https://www.npmjs.com/package/" + reportFetchResponse.api_response?.data?.raw_signals?.package_info?.pkge_name} target="_blank">
                                                        <FaNpm className="reg-icon" id="npm-icon"/>
                                                    </a>
                                                    {reportFetchResponse.api_response?.data?.raw_signals?.package_info?.latest_version_info?.repository?.url &&
                                                        <a href={"https://github.com" + reportFetchResponse.api_response?.data?.raw_signals?.package_info?.latest_version_info?.repository?.url.split("github.com")[1]} target="_blank">
                                                            <FaSquareGithub className="reg-icon" id="github-icon"/>
                                                        </a>
                                                    }
                                                </div>
                                            </div>
                                            <div className="health-score-container">
                                                <HealthScoreRadialChart input={reportFetchResponse.api_response?.data?.["overall"]}/>
                                            </div>
                                        </div>
                                        <div className="quick-look-container">
                                            <div className="container-one">
                                                <div id="sub-scores">
                                                    <SubScoreChart category={"Activity"} score={reportFetchResponse.api_response?.data?.["activity"]}/>
                                                    <SubScoreChart category={"Maintenence"} score={reportFetchResponse.api_response?.data?.["maintenance"]}/>
                                                    <SubScoreChart category={"Adoption"} score={reportFetchResponse.api_response?.data?.["adoption"]}/>
                                                    <SubScoreChart category={"Risk"} score={reportFetchResponse.api_response?.data?.["risk"]}/>
                                                </div>
                                                <MaintenanceQuickLook data={reportFetchResponse.api_response}/>
                                            </div>
                                            <div className="container-two">
                                                <CommunityQuickLook data={reportFetchResponse.api_response}/>
                                            </div>
                                        </div>
                                    </section>
                                    <section id="community-section">
                                        <DownloadsChart data={reportFetchResponse.api_response?.data?.raw_signals?.downloads_timeline}/>
                                    </section>
                                    <section id="maintenance-section">
                                        <CommitsChart data={reportFetchResponse.api_response?.data?.raw_signals?.git_hub_reg?.commit_info?.commit_timeline_volume}/>
                                        <div className="quick-look-container">
                                            <ContributorsQuickLook data={reportFetchResponse.api_response}/>
                                            <RepositoryQuickLook data={reportFetchResponse.api_response}/>
                                        </div>
                                    </section>
                                    <section id="dependencies-section">
                                        <DependencyGraph dependencies={reportFetchResponse.api_response?.data?.raw_signals?.package_info.latest_version_info?.dependency_tree}/>
                                    </section>
                                </div>
                                }
                                {reportActiveNav == 2 &&
                                    <SnapShots fetchedSnapShots={fetchedSnapShots} query={query}/>
                                }
                            </div>
                        }
                        {(reportFetchResponse.loading == true || generatingReport == true) &&
                            <div className="loading-visual">
                                <p style={{"color": "#293241"}}>Grabbing Package...</p>
                                <BarLoader color="#ff6139"/>
                            </div>
                        }
                        {(reportFetchResponse.loading == false && (reportFetchResponse.api_response?.success == false || reportFetchResponse.fetchError != null)) &&
                            <div className="loading-visual">
                                <p style={{"color": "#293241"}}>Package does not exist...</p>
                            </div>
                        }
                        {snapShotsLoading == true &&
                            <div className="loading-visual">
                                <p style={{"color": "#293241"}}>Grabbing Snap Shots...</p>
                                <BarLoader color="#ff6139"/>
                            </div>
                        }
                    </div>
            </div>
        </div>
    );
}

export default ReportPage;