
DEBUG_ENABLED = False


def az_cli(args_str: str, *non_split_args):
    '''
        Executes Azure CLI command as logged user
    '''
    from azure.cli.core import get_default_cli
    args = args_str.split()
    cli = get_default_cli()
    if DEBUG_ENABLED:
        print("az " + args_str + " " + " ".join(non_split_args))
    with open('/dev/null', 'w') as out_file:
        cli.invoke(args + list(non_split_args), out_file=out_file)
        if cli.result.result is not None:
            return cli.result.result
        elif cli.result.error:
            raise cli.result.error
        return True


def is_debug_enabled():
    return DEBUG_ENABLED