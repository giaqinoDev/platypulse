import { useState, useEffect } from "react";
import useLibrariesIOFetch from "../LibrariesIOFetch/useLibrariesIOFetch";

function useNPMFetch(queriedSearch: string|null){

    const BASE_PACKAGE_URL ="https://registry.npmjs.org";
    const BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/range/last-year";
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [packageData, setPackageData] = useState<NPMPackage_Data_Primative|null>(null);
    const libIo_dependents = useLibrariesIOFetch({query: queriedSearch, platform: "NPM", setFetchError});

    interface NPMPackage_Data_Primative{
        //Package Releases:
        latest_v: string,
        latest_release_date: string,
        releases: object,

        //Dependency Structure:
        dependencies: DependencyPackage_Info|null|undefined,
        weekly_downloads_timeline: WeeklyDownloads[]|null,
        //Depth of dependency tree will be handled by the back end
    }

    interface WeeklyDownloads{
        downloads: number|null,
        start_date: string|null,
        end_date: string|null
    }

    interface DependencyPackage_Info{
        package_dependencies: DependencyInfo[]|null;
        num_dependencies: number|null,
    }
    interface DependencyInfo{
        dependency_name: string,
        dependency_v: string,
        version_info: VersionInfo|null
        //SEMVER: MAJOR.MINOR.PATCH
    }
    interface VersionInfo{
        status: "up-to-date" | "patch-behind" | "minor-behind" | "major-behind"
        deprecated: boolean,
    }

    useEffect(() => {
        if(!queriedSearch){
            return;
        }

        const fetchPackageData = async () => {
            setFetchError(null);
            setLoading(true);
            //setQueryResult(null);

            // Dev Lesson: Defining async functiosn inside useEffect() for data fetching is common practice as defining it outside of useEffect would require the function
            // to be passed as a parameter in the useEffect hook which would then cause an infinite loop as the function is a new instance on each render
            try{
                const response = await fetch(`${BASE_PACKAGE_URL}/${queriedSearch}`);
                if(response.status >= 500){
                    throw new Error("Server error")
                }
                else if(response.status >= 400){
                    throw new Error("Client side error")
                }
                const packageInfo = await response.json();
                const latest_release = packageInfo["dist-tags"].latest;

                const queriedPackageData: NPMPackage_Data_Primative = {
                    latest_v: latest_release,
                    latest_release_date: packageInfo.time[`${latest_release}`],
                    releases: packageInfo.time,
                    
                    dependencies: await analyzeDependencies(packageInfo.versions[`${latest_release}`].dependencies),
                    weekly_downloads_timeline: await fetchDownloadData()
                    
                }
                
                setPackageData(queriedPackageData);
            }
            catch(responseErorr: unknown){
                if(responseErorr instanceof Error){
                    setFetchError("fetchPackageData: " + responseErorr);
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
            try{
                const response = await fetch(`${BASE_DOWNLOAD_URL}/${queriedSearch}`)
                const downloadsInfo = await response.json();
                const downloadTimeLine = downloadsInfo.downloads;

                let weekDay_count = 0;
                let timeline_index = 0;
                let downloads_sum = 0;
                const weekly_timeline : WeeklyDownloads[]|null = [];
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
                return weekly_timeline;
            }
            catch(responseError: unknown){
                if(responseError instanceof Error){
                    setFetchError("fetchDownloadData: " + responseError);
                }
                else{
                    setFetchError("Unkown Error");
                }
                return null;
            }
        }

        async function analyzeDependencies(dependencies_obj: Record<string,string>){
            
            async function analyzeVersionStatus(key: string, version_usage: string){
                try{
                    const response = await fetch(`${BASE_PACKAGE_URL}/${encodeURI(key)}`)
                    if(response.status >= 500){
                        setFetchError("Server side Error")
                    }
                    else if(response.status >= 400){
                        setFetchError("client side error")
                    }
                    const packageInfo = await response.json();
                    const latest_v = packageInfo["dist-tags"].latest;
                    const info: VersionInfo = {
                        status: "up-to-date",
                        deprecated: packageInfo.versions[`${version_usage}`].deprecated ? true : false
                    }
                    return info;
                }
                catch(responsError: unknown){
                    if(responsError instanceof Error){
                        setFetchError("outOfDateStatus:" + responsError.message)
                    }
                    else{
                        setFetchError("Unkown Error")
                    }
                }
                return null;
            }

            //In the case a package has no dependencies the api response will not include a dependencies obj
            if(!dependencies_obj){
                return { 
                    package_dependencies: null, 
                    num_dependencies: 0
                };
            }

            const dependency_list: DependencyPackage_Info = {
                package_dependencies: null,
                num_dependencies: null,
            };
            const dependency_info: DependencyInfo[] = [];
            let dependency_count = 0;

            for(const key of Object.keys(dependencies_obj)){
                dependency_info.push({
                    dependency_name: key,
                    dependency_v: dependencies_obj[`${key}`],
                    version_info: await analyzeVersionStatus(key, dependencies_obj[`${key}`].slice(1))
                });
                dependency_count++;
            }

            dependency_list.package_dependencies = dependency_info;
            dependency_list.num_dependencies = dependency_count;

            return dependency_list;
        }
        fetchPackageData();
    }, [queriedSearch])

    if(packageData){
        //console.log(packageData.dependencies)
    }
    return { packageData, libIo_dependents, fetchError, loading };
}

export default useNPMFetch;