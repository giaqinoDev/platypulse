import { useState, useEffect } from "react";

function useNPMFetch(queriedSearch: string|null){

    const BASE_PACKAGE_URL ="https://registry.npmjs.org";
    const BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/range/last-year";
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>();
    const [packageData, setPackageData] = useState<NPMPackage_Data_Primative|null>(null);
    const [weekly_download_info, setWeekly_download_info] = useState<WeeklyDownloads[]|null>(null);

    interface NPMPackage_Data_Primative{
        //Package Releases:
        latest_v: string,
        releases: object,

        //Dependency Structure:
        dir_dependency_count: number,
        weekly_downloads_timeline: WeeklyDownloads[]|null,
        //Depth of dependency tree will be handled by the back end
    }

    interface WeeklyDownloads{
        downloads: number|null,
        start_date: string|null,
        end_date: string|null
    }

    useEffect(() => {
        if(!queriedSearch){
            return;
        }

        const fetchPackageData = async () => {
            setFetchError(null);
            //setQueryResult(null);

            // Dev Lesson: Defining async functiosn inside useEffect() for data fetching is common practice as defining it outside of useEffect would require the function
            // to be passed as a parameter in the useEffect hook which would then cause an infinite loop as the function is a new instance on each render
            try{
                //Using CORS Proxy Server
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_PACKAGE_URL}/${queriedSearch}`));
                if(response.status >= 500){
                    throw new Error("Server error")
                }
                else if(response.status >= 400){
                    throw new Error("Client side error")
                }
                const packageInfo = await response.json();
                const latest_release = packageInfo["dist-tags"].latest;


                await fetchDownloadData(); //NEED TO REFACTOR LOGICAL FLOW OF THIS API CALL

                //In the case a package has no dependencies the api response will not include a dependencies obj
                //this is accoutned for below
                const arr_of_dependencies = packageInfo.versions[`${latest_release}`].dependencies;
                let dependency_arr_len = 0;
                if(arr_of_dependencies){
                    dependency_arr_len = Object.keys(arr_of_dependencies).length;
                }
                const queriedPackageData: NPMPackage_Data_Primative = {
                    latest_v: latest_release,
                    releases: packageInfo.time,

                    dir_dependency_count: dependency_arr_len,
                    weekly_downloads_timeline: weekly_download_info
                    
                }
                
                setPackageData(queriedPackageData);
            }
            catch(responseErorr: unknown){
                if(responseErorr instanceof Error){
                    setFetchError("fetchPackageData: " + responseErorr);
                    console.log(responseErorr.stack)
                }
                else{
                    setFetchError("Unkown Error");
                }
            }
            finally{
                setLoading(false);
            }
        }

        const fetchDownloadData = async () => {
            setLoading(true);
            try{
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_DOWNLOAD_URL}/${queriedSearch}`))
                const downloadsInfo = await response.json();
                const downloadTimeLine = downloadsInfo.downloads;

                let weekDay_count = 0;
                let timeline_index = 0;
                let downloads_sum = 0;
                const weekly_timeline : WeeklyDownloads[] = [];
                const numWeeks: number = Math.ceil(downloadTimeLine.length/7);

                //Populate timeline with weeks
                for(let i: number = 0; i < numWeeks; i++){
                    weekly_timeline.push({
                        downloads: 0,
                        start_date: null,
                        end_date: null
                    });
                }

                downloadTimeLine.map((dataInfo: typeof downloadTimeLine) => {
                    downloads_sum += dataInfo.downloads;
                    if(weekDay_count == 0){
                        weekly_timeline[timeline_index].start_date = dataInfo.day;
                        weekDay_count++;
                    }
                    else if (weekDay_count == 6){
                        weekly_timeline[timeline_index].end_date = dataInfo.day;
                        weekly_timeline[timeline_index].downloads = downloads_sum;
                        downloads_sum = 0;
                        timeline_index++;
                        weekDay_count = 0;
                    }
                    else{
                        weekDay_count++;
                    }
                });
                console.log(weekly_timeline);
                setWeekly_download_info(weekly_timeline);

            }
            catch(responseError: unknown){
                if(responseError instanceof Error){
                    setFetchError("fetchDownloadData: " + responseError);
                }
                else{
                    setFetchError("Unkown Error");
                }
            }
        }

        fetchPackageData();
    }, [queriedSearch])

    return { packageData, fetchError, loading };
}

export default useNPMFetch;