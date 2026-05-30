import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
function useNPMFetch(queriedSearch: SearchContextStructure|null){
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [packageData, setPackageData] = useState<JSON|null>(null);

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
        setLoading(true);
        const fetchFromBackend = async () => {
            try{
                let response = null;
                if(queriedSearch.search_term.length == 2){
                    response = await fetch(`/bend/api/packages/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}`);
                }
                else if(queriedSearch.search_term.length == 1){
                    response = await fetch(`/bend/api/packages/${queriedSearch.search_term[0]}`);
                }
                console.log(response.status);
                const info = await response.json();
                setPackageData(info);
                setLoading(false);
            }
            catch(reponseError: unknown){
                if(reponseError instanceof Error){
                    setFetchError("fetchFromBackend: " + reponseError);
                }
                else{
                    setFetchError("Unkown Error");
                }
                setLoading(false);
            }
        }
        fetchFromBackend();
    }, [queriedSearch])
    return {packageData, loading, fetchError}
}
export default useNPMFetch;